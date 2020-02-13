package com.geoxus.core.common.builder;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXBaseBuilderConstants;
import com.geoxus.core.common.entity.GXBaseEntity;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.framework.service.GXCoreModelService;
import com.geoxus.core.framework.service.GXDBSchemaService;
import com.google.common.collect.Table;
import org.apache.ibatis.jdbc.SQL;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface GXBaseBuilder {
    @GXFieldCommentAnnotation(zh = "模型的值")
    String MODEL_IDENTIFICATION_VALUE = "";

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
     * @param tableName
     * @param data
     * @param whereData
     * @return
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
     * @return
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

    String listOrSearch(Dict param);

    String detail(Dict param);

    /**
     * 获取请求对象中的搜索条件数据
     *
     * @param param
     * @return
     */
    default Dict getRequestSearchCondition(Dict param) {
        return Optional.ofNullable(Convert.convert(Dict.class, param.getObj(GXBaseBuilderConstants.SEARCH_CONDITION_NAME))).orElse(Dict.create());
    }

    /**
     * 组合时间查询SQL
     * <pre>
     *     {@code
     *     processTimeField("created_at", "created_at > {} and created_at < {}", {start=2019-12-20,end=2019-12-31})
     *     }
     * </pre>
     *
     * @param fieldName
     * @param conditionStr
     * @param param
     * @return
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
     * @param sql
     * @param requestParam
     * @return
     */
    default Dict mergeSearchConditionToSQL(SQL sql, Dict requestParam) {
        final Dict condition = Dict.create().set(GXBaseBuilderConstants.MODEL_IDENTIFICATION_NAME, getModelIdentificationValue());
        Dict searchField = GXSpringContextUtils.getBean(GXCoreModelService.class).getSearchCondition(condition);
        searchField.putAll(getDefaultSearchField());
        Dict searchCondition = getRequestSearchCondition(requestParam);
        Set<String> keySet = searchCondition.keySet();
        for (String key : keySet) {
            if (null != searchCondition.getObj(key)) {
                String operator = searchField.getStr(key);
                if (StrUtil.isNotBlank(GXBaseBuilderConstants.TIME_FIELDS.getStr(key))) {
                    if (null == operator) {
                        operator = GXBaseBuilderConstants.TIME_FIELDS.getStr(key);
                    }
                    final String s = processTimeField(key, operator, searchCondition.getObj(key));
                    if (null != s) {
                        sql.WHERE(s);
                    }
                    continue;
                }
                sql.WHERE(StrUtil.format("{} ".concat(operator), key, searchCondition.getObj(key)));
            }
        }
        return Dict.create();
    }

    /**
     * 获取SQL语句的查询字段
     *
     * @param tableName
     * @param targetSet
     * @param tableAlias
     * @param remove
     * @return
     */
    default String getSelectFieldStr(String tableName, Set<String> targetSet, String tableAlias, boolean remove) {
        return GXSpringContextUtils.getBean(GXDBSchemaService.class).getSqlFieldStr(tableName, targetSet, tableAlias, remove);
    }

    /**
     * 获取SQL语句的查询字段
     *
     * @param tableName
     * @param targetSet
     * @param remove
     * @return
     */
    default String getSelectFieldStr(String tableName, Set<String> targetSet, boolean remove) {
        return GXSpringContextUtils.getBean(GXDBSchemaService.class).getSqlFieldStr(tableName, targetSet, remove);
    }

    default Dict getDefaultSearchField() {
        return Dict.create();
    }

    default String getModelIdentificationValue() {
        return MODEL_IDENTIFICATION_VALUE;
    }
}
