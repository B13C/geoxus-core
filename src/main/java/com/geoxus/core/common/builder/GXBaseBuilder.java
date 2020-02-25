package com.geoxus.core.common.builder;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.ReUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXBaseBuilderConstants;
import com.geoxus.core.common.entity.GXBaseEntity;
import com.geoxus.core.common.util.GXCommonUtils;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.framework.service.GXCoreModelService;
import com.geoxus.core.framework.service.GXDBSchemaService;
import com.google.common.collect.Table;
import org.apache.ibatis.jdbc.SQL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public interface GXBaseBuilder {
    @GXFieldCommentAnnotation(zh = "LOG对象")
    Logger LOG = LoggerFactory.getLogger(GXBaseBuilder.class);

    /**
     * 更新实体字段和虚拟字段
     * <pre>
     *   {@code
     *    final Table<String, String, Object> extData = HashBasedTable.create();
     *    extData.put("ext", "name", "jack");
     *    extData.put("ext", "address", "四川成都");
     *    extData.put("ext", "-salary" , "")
     *    final Dict data = Dict.create().set("category_name", "打折商品").set("ext", extData);
     *    updateFieldByCondition("s_category", data, Dict.create().set("category_id", 2));
     *  }
     *  </pre>
     *
     * @param tableName 表名
     * @param data      数据
     * @param whereData 条件
     * @return String
     */
    @SuppressWarnings("unused")
    static String updateFieldByCondition(String tableName, Dict data, Dict whereData) {
        final SQL sql = new SQL().UPDATE(tableName);
        final Set<String> dataKeys = data.keySet();
        for (String dataKey : dataKeys) {
            final Object value = data.getObj(dataKey);
            if (value instanceof Table) {
                Table<String, String, Object> table = Convert.convert(new TypeReference<Table<String, String, Object>>() {
                }, value);
                final Map<String, Object> row = table.row(dataKey);
                for (Map.Entry<String, Object> entry : row.entrySet()) {
                    final String entryKey = entry.getKey();
                    Object entryValue = entry.getValue();
                    if (entryKey.startsWith("-")) {
                        sql.SET(StrUtil.format("{} = JSON_REMOVE({} , '$.{}')", dataKey, dataKey, entryKey.substring(1)));
                    } else {
                        if (entryValue instanceof Number) {
                            sql.SET(StrUtil.format("{} = JSON_SET({} , '$.{}' , {})", dataKey, dataKey, entryKey, entryValue));
                        } else {
                            if (!ClassUtil.isPrimitiveWrapper(entryValue.getClass()) && !ClassUtil.equals(entryValue.getClass(), "String", true) && (entryValue instanceof Map || entryValue instanceof GXBaseEntity)) {
                                entryValue = JSONUtil.toJsonStr(entryValue);
                            }
                            sql.SET(StrUtil.format("{} = JSON_SET({} , '$.{}' , '{}')", dataKey, dataKey, entryKey, entryValue));
                        }
                    }
                }
                continue;
            }
            sql.SET(StrUtil.format("{} = '{}'", dataKey, value));
        }
        final Set<String> conditionKeys = whereData.keySet();
        for (String conditionKey : conditionKeys) {
            String template = "{} = '{}'";
            final Object value = whereData.getObj(conditionKey);
            if (value instanceof Number) {
                template = "{} = {}";
            }
            sql.WHERE(StrUtil.format(template, conditionKey, value));
        }
        sql.SET(StrUtil.format("updated_at = {}", DateUtil.currentSeconds()));
        return sql.toString();
    }

    /**
     * 单独更新记录状态
     *
     * @param tableName 数据表明
     * @param status    状态值
     * @param operator  操作
     * @param condition 更新条件
     * @return String
     */
    static String updateStatus(String tableName, int status, Dict condition, String operator) {
        final SQL sql = new SQL().UPDATE(tableName);
        if (GXBaseBuilderConstants.OR_OPERATOR.equals(operator)) {
            sql.SET(StrUtil.format("`status` = `status` | {}", status));
        } else if (GXBaseBuilderConstants.NEGATION_OPERATOR.equals(operator)) {
            sql.SET(StrUtil.format("`status` = `status` & ~{}", status));
        } else {
            sql.SET(StrUtil.format("`status` = {}", status));
        }
        final Set<String> conditionKeys = condition.keySet();
        for (String conditionKey : conditionKeys) {
            String template = "{} = '{}'";
            final Object value = condition.getObj(conditionKey);
            if (value instanceof Number) {
                template = "{} = {}";
            }
            sql.WHERE(StrUtil.format(template, conditionKey, value));
        }
        return sql.toString();
    }

    /**
     * 判断给定条件的值是否存在
     *
     * @param tableName 表名
     * @param condition 条件
     * @return String
     */
    static String checkRecordIsExists(String tableName, Dict condition) {
        final SQL sql = new SQL().SELECT("1").FROM(tableName);
        final Set<String> conditionKeys = condition.keySet();
        for (String conditionKey : conditionKeys) {
            String template = "{} = '{}'";
            final Object value = condition.getObj(conditionKey);
            if (ReUtil.getFirstNumber(value.toString()) instanceof Number) {
                template = "{} = {}";
            }
            sql.WHERE(StrUtil.format(template, conditionKey, value));
        }
        sql.LIMIT(1);
        return StrUtil.format("SELECT IFNULL(({}) , NULL)", sql.toString());
    }

    /**
     * 查询单表的指定字段
     *
     * @param tableName 表名
     * @param fieldSet  字段集合
     * @param condition 条件
     * @return String
     */
    static String getFieldBySQL(String tableName, Set<String> fieldSet, Dict condition) {
        final SQL sql = new SQL().SELECT(CollUtil.join(fieldSet, ",")).FROM(tableName);
        final Set<String> conditionKeys = condition.keySet();
        for (String conditionKey : conditionKeys) {
            String template = "{} = '{}'";
            final Object value = condition.getObj(conditionKey);
            if (value instanceof Number) {
                template = "{} = {}";
            }
            sql.WHERE(StrUtil.format(template, conditionKey, value));
        }
        return sql.toString();
    }

    /**
     * 通过SQL语句批量插入数据
     *
     * @param tableName 表名
     * @param fieldSet  字段集合
     * @param dataList  需要插入的数据列表
     * @return
     */
    static String batchInsertBySQL(String tableName, Set<String> fieldSet, List<Dict> dataList) {
        String sql = "INSERT INTO " + tableName + "(" + CollUtil.join(fieldSet, ",") + ") VALUES ";
        StringBuilder values = new StringBuilder();
        for (Dict dict : dataList) {
            values.append("(");
            for (String field : fieldSet) {
                values.append(dict.getStr(field)).append(",");
            }
            values.deleteCharAt(values.lastIndexOf(",")).append("),");
        }
        return sql + StrUtil.sub(values, 0, values.lastIndexOf(","));
    }

    /**
     * 列表
     *
     * @param param 参数
     * @return String
     */
    String listOrSearch(Dict param);

    /**
     * 详情
     *
     * @param param 参数
     * @return String
     */
    String detail(Dict param);

    /**
     * 获取请求对象中的搜索条件数据
     *
     * @param param 参数
     * @return Dict
     */
    default Dict getRequestSearchCondition(Dict param) {
        return Optional.ofNullable(Convert.convert(Dict.class, param.getObj(GXBaseBuilderConstants.SEARCH_CONDITION_NAME))).orElse(param);
    }

    /**
     * 组合时间查询SQL
     * <pre>
     *     {@code
     *     processTimeField("created_at", "created_at > {} and created_at < {}", {start=2019-12-20,end=2019-12-31})
     *     }
     * </pre>
     *
     * @param fieldName    字段名字
     * @param conditionStr 查询条件
     * @param param        参数
     * @return String
     */
    default String processTimeField(String fieldName, String conditionStr, Object param) {
        final String today = DateUtil.today();
        String startDate = StrUtil.format("{} 0:0:0", today);
        String endDate = StrUtil.format("{} 23:59:59", today);
        final Dict dict = Convert.convert(Dict.class, param);
        if (null != dict.getStr("start")) {
            startDate = dict.getStr("start");
        }
        if (null != dict.getStr("end")) {
            endDate = dict.getStr("end");
        }
        final long start = DateUtil.parse(startDate).getTime() / 1000;
        final long end = DateUtil.parse(endDate).getTime() / 1000;
        return StrUtil.format(conditionStr, fieldName, start, fieldName, end);
    }

    /**
     * 合并搜索条件到SQL对象中
     *
     * @param sql          SQL对象
     * @param requestParam 请求参数
     * @param aliasPrefix  别名
     * @return
     */
    default Dict mergeSearchConditionToSQL(SQL sql, Dict requestParam, String aliasPrefix) {
        final String modelIdentificationValue = getModelIdentificationValue();
        if (StrUtil.isBlank(modelIdentificationValue)) {
            LOG.error("请配置{}.{}的模型标识", getClass().getSimpleName(), GXBaseBuilderConstants.MODEL_IDENTIFICATION_NAME);
        }
        final Dict condition = Dict.create().set(GXBaseBuilderConstants.MODEL_IDENTIFICATION_NAME, modelIdentificationValue);
        Dict searchField = GXSpringContextUtils.getBean(GXCoreModelService.class).getSearchCondition(condition);
        searchField.putAll(getDefaultSearchField());
        Dict searchCondition = getRequestSearchCondition(requestParam);
        Set<String> keySet = searchCondition.keySet();
        for (String key : keySet) {
            Object value = searchCondition.getObj(key);
            if (null != value) {
                String operator = searchField.getStr(key);
                if (null == operator && StrUtil.isNotBlank(aliasPrefix)) {
                    operator = searchField.getStr(StrUtil.concat(true, aliasPrefix, ".", key));
                }
                if (StrUtil.isNotBlank(GXBaseBuilderConstants.TIME_FIELDS.getStr(key))) {
                    if (null == operator) {
                        operator = GXBaseBuilderConstants.TIME_FIELDS.getStr(key);
                    }
                    final String s = processTimeField(key, operator, value);
                    if (null != s) {
                        sql.WHERE(s);
                    }
                    continue;
                }
                if (null == operator) {
                    LOG.warn(StrUtil.format("{}字段没有配置搜索条件", key));
                    continue;
                }
                if (value instanceof Collection) {
                    value = CollUtil.join((Collection<?>) value, ",");
                }
                if (StrUtil.isNotBlank(aliasPrefix)) {
                    sql.WHERE(StrUtil.format("`{}`.`{}` ".concat(operator), aliasPrefix, key, value));
                } else {
                    if (StrUtil.contains(key, ".")) {
                        sql.WHERE(StrUtil.format("{} ".concat(operator), key, value));
                    } else {
                        sql.WHERE(StrUtil.format("`{}` ".concat(operator), key, value));
                    }
                }
            }
        }
        return Dict.create();
    }

    /**
     * 合并搜索条件到SQL对象中
     *
     * @param sql          SQL对象
     * @param requestParam 请求参数
     * @return
     */
    default Dict mergeSearchConditionToSQL(SQL sql, Dict requestParam) {
        return mergeSearchConditionToSQL(sql, requestParam, "");
    }

    /**
     * 获取SQL语句的查询字段
     *
     * @param tableName  表名
     * @param targetSet  目标字段集合
     * @param tableAlias 表的别名
     * @param remove     是否移除
     * @return String
     */
    default String getSelectFieldStr(String tableName, Set<String> targetSet, String tableAlias, boolean remove) {
        return GXSpringContextUtils.getBean(GXDBSchemaService.class).getSqlFieldStr(tableName, targetSet, tableAlias, remove);
    }

    /**
     * 获取SQL语句的查询字段
     *
     * @param tableName 表名
     * @param targetSet 目标字段集合
     * @param remove    是否移除
     * @return String
     */
    default String getSelectFieldStr(String tableName, Set<String> targetSet, boolean remove) {
        return GXSpringContextUtils.getBean(GXDBSchemaService.class).getSqlFieldStr(tableName, targetSet, remove);
    }

    /**
     * 给现有查询条件新增查询条件
     *
     * @param requestParam 请求参数
     * @param key          key
     * @param value        value
     * @return Dict
     */
    default Dict addConditionToSearchCondition(Dict requestParam, String key, Object value) {
        return GXCommonUtils.addConditionToSearchCondition(requestParam, key, value, false);
    }

    /**
     * 给现有查询条件新增查询条件
     *
     * @param requestParam 请求参数
     * @param sourceData   数据源
     * @return Dict
     */
    default Dict addConditionToSearchCondition(Dict requestParam, Dict sourceData) {
        return GXCommonUtils.addConditionToSearchCondition(requestParam, sourceData, false);
    }

    /**
     * 默认的搜索条件
     *
     * @return Dict
     */
    Dict getDefaultSearchField();

    /**
     * 数据配置的模型标识
     *
     * @return String
     */
    String getModelIdentificationValue();
}
