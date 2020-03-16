package com.geoxus.core.framework.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.event.GXBaseEvent;
import com.geoxus.core.common.event.GXMediaLibraryEvent;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.mapper.GXBaseMapper;
import com.geoxus.core.common.util.GXCommonUtils;
import com.geoxus.core.common.util.GXHttpContextUtils;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.framework.entity.GXCoreMediaLibraryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.*;

/**
 * 业务基础Service
 *
 * @param <T>
 * @author britton chen <britton@126.com>
 */
public interface GXBaseService<T> extends IService<T> {
    @GXFieldCommentAnnotation(zh = "日志对象")
    Logger log = LoggerFactory.getLogger(GXBaseService.class);

    /**
     * 获取当前接口的常量字段信息
     *
     * @return Dict
     */
    @SuppressWarnings("unused")
    default Dict getConstantsFields() {
        final Dict data = Dict.create();
        final ArrayList<Class<?>> clazzInterfaces = new ArrayList<>();
        GXCommonUtils.getInterfaces(getClass(), clazzInterfaces);
        for (Class<?> clz : clazzInterfaces) {
            GXCommonUtils.clazzFields(clz, data);
        }
        return data;
    }

    /**
     * 获取实体中指定指定的值
     * <pre>
     *     {@code
     *     getSingleJSONFieldValueByDB(
     *      GoodsEntity,
     *      "ext.name",
     *      Integer.class,
     *      Dict.create().set("user_id" , 1111)
     *      )
     *     }
     * </pre>
     *
     * @param clazz     Class对象
     * @param path      路径
     * @param condition 条件
     * @return R
     */
    default <R> R getSingleJSONFieldValueByDB(Class<T> clazz, String path, Class<R> type, Dict condition) {
        return getSingleJSONFieldValueByDB(clazz, path, condition, type, GXCommonUtils.getClassDefaultValue(type));
    }

    /**
     * 获取实体中指定指定的值
     *
     * @param clazz        Class对象
     * @param path         路径
     * @param condition    条件
     * @param defaultValue 默认值
     * @return R
     */
    default <R> R getSingleJSONFieldValueByDB(Class<T> clazz, String path, Dict condition, Class<R> type, R defaultValue) {
        String aliasName = path;
        if (StrUtil.contains(path, ".")) {
            aliasName = StrUtil.split(path, ".")[1].replace("'", "");
            path = StrUtil.format("{} as `{}`", path, aliasName);
        }
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        final Dict dict = baseMapper.getFieldBySQL(getTableName(clazz), CollUtil.newHashSet(path), condition, false);
        if (null == dict) {
            return defaultValue;
        }
        return Convert.convert(type, dict.get(aliasName, defaultValue));
    }

    /**
     * 获取JSON中的多个值
     *
     * @param clazz     Class 对象
     * @param fields    字段
     * @param condition 条件
     * @return Dict
     */
    @SuppressWarnings("unused")
    default Dict getMultiJSONFieldValueByDB(Class<T> clazz, Map<String, Class<?>> fields, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        final HashSet<String> fieldSet = CollUtil.newHashSet();
        final Dict dataKey = Dict.create();
        for (Map.Entry<String, Class<?>> entry : fields.entrySet()) {
            String key = entry.getKey();
            String aliasName = key;
            if (StrUtil.contains(key, ".")) {
                aliasName = StrUtil.format("ext_{}", StrUtil.split(key, ".")[1].replace("'", "").concat(RandomUtil.randomString(5)));
                fieldSet.add(StrUtil.format("{} as `{}`", key, aliasName));
            } else {
                fieldSet.add(StrUtil.format("`{}`", key));
            }
            dataKey.set(aliasName, key);
        }
        final Dict dict = baseMapper.getFieldBySQL(getTableName(clazz), fieldSet, condition, false);
        final Dict retDict = Dict.create();
        for (Map.Entry<String, Object> entry : dataKey.entrySet()) {
            Object value = dict.getObj(entry.getKey());
            if (value instanceof byte[]) {
                value = new String((byte[]) value, StandardCharsets.UTF_8);
            }
            final Class<?> aClass = fields.get(entry.getValue());
            retDict.set((String) entry.getValue(), Convert.convert(aClass, value, GXCommonUtils.getClassDefaultValue(aClass)));
        }
        return retDict;
    }

    /**
     * 获取实体中指定指定的值
     * <pre>
     *     {@code
     *     getSingleJSONFieldValueByEntity(
     *       GoodsEntity,
     *       "ext.name",
     *       Integer.class
     *       )
     *     }
     * </pre>
     *
     * @param entity 实体对象
     * @param path   路径
     * @return R
     */
    default <R> R getSingleJSONFieldValueByEntity(T entity, String path, Class<R> type) {
        return getSingleJSONFieldValueByEntity(entity, path, type, GXCommonUtils.getClassDefaultValue(type));
    }

    /**
     * 获取实体中指定指定的值
     * <pre>
     *     {@code
     *     getSingleJSONFieldValueByEntity(
     *       GoodsEntity,
     *       "ext.name",
     *       Integer.class
     *       0
     *       )
     *     }
     * </pre>
     *
     * @param entity       实体对象
     * @param path         路径
     * @param defaultValue 默认值
     * @return R
     */
    default <R> R getSingleJSONFieldValueByEntity(T entity, String path, Class<R> type, R defaultValue) {
        JSON json = JSONUtil.parse(JSONUtil.toJsonStr(entity));
        int index = StrUtil.indexOf(path, '.');
        if (index == -1) {
            if (null == json.getByPath(path)) {
                return defaultValue;
            }
            return Convert.convert(type, json.getByPath(path));
        }
        String mainField = StrUtil.sub(path, 0, index);
        if (null == json.getByPath(mainField)) {
            throw new GXException(StrUtil.format("实体的主字段{}不存在!", mainField));
        }
        String subField = StrUtil.sub(path, index + 1, path.length());
        JSON parse = JSONUtil.parse(json.getByPath(mainField));
        if (null == parse) {
            return defaultValue;
        }
        return Convert.convert(type, parse.getByPath(subField), defaultValue);
    }

    /**
     * 获取实体的多个JSON值
     *
     * @param entity 实体对象
     * @param dict   需要获取的数据
     * @return Dict
     */
    default Dict getMultiJSONFieldValueByEntity(T entity, Dict dict) {
        final Set<String> keySet = dict.keySet();
        final Dict data = Dict.create();
        for (String key : keySet) {
            final Object value = getSingleJSONFieldValueByEntity(entity, key, (Class<?>) dict.getObj(key));
            final String[] strings = StrUtil.split(key, StrUtil.DOT);
            data.set(strings[strings.length - 1], value);
        }
        return data;
    }

    /**
     * 获取实体对象的媒体文件
     *
     * @param modelId     实体对象模型ID
     * @param coreModelId 实体模型ID
     * @param param       其他参数
     * @return Collection
     */
    default Collection<GXCoreMediaLibraryEntity> getMedia(int modelId, int coreModelId, Dict param) {
        final GXCoreMediaLibraryService mediaLibraryService = GXSpringContextUtils.getBean(GXCoreMediaLibraryService.class);
        assert mediaLibraryService != null;
        return mediaLibraryService.listByMap(param.set("model_id", modelId).set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId));
    }

    /**
     * 更新JSON字段中的某一个值
     *
     * @param clazz     Class对象
     * @param path      路径
     * @param value     值
     * @param condition 条件
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateJSONFieldSingleValue(Class<T> clazz, String path, Object value, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        int index = StrUtil.indexOf(path, '.');
        String mainPath = StrUtil.sub(path, 0, index);
        String subPath = StrUtil.sub(path, index + 1, path.length());
        final Dict data = Dict.create().set(mainPath, Dict.create().set(subPath, value));
        return baseMapper.updateFieldByCondition(getTableName(clazz), data, condition);
    }

    /**
     * 更新JSON字段中的某多个值
     *
     * @param clazz     Class对象
     * @param data      需要更新的数据
     * @param condition 更新条件
     * @return boolean
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateJSONFieldMultiValue(Class<T> clazz, Dict data, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.updateFieldByCondition(getTableName(clazz), data, condition);
    }

    /**
     * 获取Cache对象
     *
     * @param cacheName 缓存名字
     * @return Cache
     */
    default Cache getCache(String cacheName) {
        return GXCommonUtils.getSpringCache(cacheName);
    }

    /**
     * 处理用户上传的资源文件
     *
     * @param target  目标对象
     * @param modelId 模型ID
     * @param param   参数
     */
    default void handleMedia(T target, long modelId, @NotNull Dict param) {
        final List<Integer> media = Convert.convert(new TypeReference<List<Integer>>() {
        }, GXHttpContextUtils.getHttpParam("media_info", List.class));
        if (null != media) {
            param.set("media", media);
            param.set("model_id", modelId);
            final GXMediaLibraryEvent<T> event = new GXMediaLibraryEvent<>(target, param);
            publishEvent(event);
        }
    }

    /**
     * 根据条件获取一条记录
     *
     * @param clazz     Class对象
     * @param fieldSet  字段集合
     * @param condition 查询条件
     * @return Dict
     */
    @SuppressWarnings("unused")
    default Dict getOneByCondition(Class<T> clazz, Set<String> fieldSet, Dict condition) {
        return getFieldBySQL(clazz, fieldSet, condition);
    }

    /**
     * 检测给定条件的记录是否存在
     *
     * @param clazz     实体的Class
     * @param condition 条件
     * @return int
     */
    default Integer checkRecordIsExists(Class<T> clazz, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.checkRecordIsExists(getTableName(clazz), condition);
    }

    /**
     * 检测给定条件的记录是否唯一
     *
     * @param clazz     实体的Class
     * @param condition 条件
     * @return int
     */
    default Integer checkRecordIsUnique(Class<T> clazz, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.checkRecordIsUnique(getTableName(clazz), condition);
    }

    /**
     * 获取实体的表明
     *
     * @param clazz Class对象
     * @return String
     */
    default String getTableName(Class<T> clazz) {
        return GXCommonUtils.getTableName(clazz);
    }

    /**
     * 修改状态
     *
     * @param status    状态
     * @param condition 条件
     * @return boolean
     */
    default boolean modifyStatus(int status, Dict condition) {
        final Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Class<T> clazz = Convert.convert(new TypeReference<Class<T>>() {
        }, type);
        return updateStatusBySQL(clazz, status, condition);
    }

    /**
     * 通过SQL更新表中的数据
     *
     * @param clazz     Class 对象
     * @param data      需要更新的数据
     * @param condition 更新条件
     * @return boolean
     */
    default boolean updateFieldBySQL(Class<T> clazz, Dict data, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.updateFieldByCondition(getTableName(clazz), data, condition);
    }

    /**
     * 通过SQL更新表中的数据
     *
     * @param clazz     Class 对象
     * @param status    状态
     * @param condition 更新条件
     * @return boolean
     */
    default boolean updateStatusBySQL(Class<T> clazz, int status, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.updateStatusByCondition(getTableName(clazz), status, condition);
    }

    /**
     * 通过SQL语句批量插入数据
     *
     * @param clazz    实体的Class
     * @param fieldSet 字段集合
     * @param dataList 数据集合
     * @return int
     */
    @SuppressWarnings("unused")
    default Integer batchInsertBySQL(Class<T> clazz, Set<String> fieldSet, List<Dict> dataList) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.batchInsertBySQL(getTableName(clazz), fieldSet, dataList);
    }

    /**
     * 获取表中的指定字段
     *
     * @param clazz     Class对象
     * @param fieldSet  字段集合
     * @param condition 查询条件
     * @return Dict
     */
    default Dict getFieldBySQL(Class<T> clazz, Set<String> fieldSet, Dict condition) {
        return getFieldBySQL(clazz, fieldSet, condition, false);
    }

    /**
     * 获取表中的指定字段
     *
     * @param clazz     Class对象
     * @param fieldSet  字段集合
     * @param condition 查询条件
     * @return Dict
     */
    default Dict getFieldBySQL(Class<T> clazz, Set<String> fieldSet, Dict condition, boolean remove) {
        final String tableName = getTableName(clazz);
        return getFieldBySQL(tableName, fieldSet, condition, remove);
    }

    /**
     * 获取表中的指定字段
     *
     * @param tableName 表名
     * @param fieldSet  字段集合
     * @param condition 更新条件
     * @param remove    是否移除
     * @return Dict
     */
    default Dict getFieldBySQL(String tableName, Set<String> fieldSet, Dict condition, boolean remove) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        final GXCoreModelService modelService = GXSpringContextUtils.getBean(GXCoreModelService.class);
        assert modelService != null;
        final int coreModelId = modelService.getCoreModelIdByTableName(tableName);
        final GXCoreModelAttributePermissionService permissionService = GXSpringContextUtils.getBean(GXCoreModelAttributePermissionService.class);
        assert permissionService != null;
        final Dict permissions = permissionService.getModelAttributePermissionByCoreModelId(coreModelId, Dict.create());
        final Dict dict = baseMapper.getFieldBySQL(tableName, fieldSet, condition, remove);
        final Dict jsonFieldDict = Convert.convert(Dict.class, permissions.getObj("json_field"));
        final Dict dbFieldDict = Convert.convert(Dict.class, permissions.getObj("db_field"));
        assert dict != null;
        final Set<Map.Entry<String, Object>> entries = dict.entrySet();
        final Dict retDict = Dict.create();
        for (Map.Entry<String, Object> entry : entries) {
            final String key = entry.getKey();
            final Object object = entry.getValue();
            if (null == object) {
                continue;
            }
            if ((object instanceof String) && JSONUtil.isJson((String) object)) {
                final Dict removeField = Convert.convert(Dict.class, jsonFieldDict.getObj(key));
                final Dict bean = JSONUtil.toBean((String) object, Dict.class);
                if (null != removeField) {
                    for (String removeKey : removeField.keySet()) {
                        bean.remove(removeKey);
                    }
                }
                retDict.set(key, bean);
            } else if (null == dbFieldDict.getObj(key)) {
                retDict.set(key, dict.getObj(key));
            }
        }
        return retDict;
    }

    /**
     * 记录原表的数据到历史表里面
     *
     * @param originTableName  原表名
     * @param historyTableName 历史表名字
     * @param condition        条件
     * @param appendData       附加信息
     * @return boolean
     */
    @SuppressWarnings("unused")
    @Transactional(rollbackFor = Exception.class)
    default boolean recordModificationHistory(String originTableName, String historyTableName, Dict condition, Dict appendData) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        assert baseMapper != null;
        final Dict targetDict = baseMapper.getFieldBySQL(originTableName, CollUtil.newHashSet("*"), condition, false);
        if (targetDict.isEmpty()) {
            return false;
        }
        GXAlterTableService gxAlterTableService = GXSpringContextUtils.getBean(GXAlterTableService.class);
        assert gxAlterTableService != null;
        List<GXDBSchemaService.TableField> tableColumns = CollUtil.newArrayList();
        try {
            tableColumns = gxAlterTableService.getTableColumns(historyTableName);
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        if (tableColumns.isEmpty()) {
            return false;
        }
        final Set<String> tableField = CollUtil.newHashSet();
        for (GXDBSchemaService.TableField field : tableColumns) {
            tableField.add(field.getColumnName());
        }
        final Dict tableValues = Dict.create();
        final HashSet<String> lastTableField = new HashSet<>();
        for (String key : tableField) {
            String value = targetDict.getStr(key);
            if (null != value) {
                lastTableField.add(key);
                final Object dataObj = appendData.getObj(key);
                if (null != dataObj) {
                    if (JSONUtil.isJson(value)) {
                        final Dict toBean = JSONUtil.toBean(value, Dict.class);
                        if ((dataObj instanceof Dict)) {
                            toBean.putAll((Dict) dataObj);
                        } else {
                            toBean.set(key, dataObj);
                        }
                        value = JSONUtil.toJsonStr(toBean);
                    } else {
                        value = value.concat("::" + dataObj.toString());
                    }
                }
                tableValues.set(key, value);
            }
        }
        tableValues.set("updated_at", tableValues.getInt("created_at"));
        tableValues.set("created_at", DateUtil.currentSeconds());
        final Integer insert = baseMapper.batchInsertBySQL(historyTableName, lastTableField, CollUtil.newArrayList(tableValues));
        return insert > 0;
    }

    /**
     * 派发同步事件
     *
     * @param event ApplicationEvent对象
     */
    default <R> void publishEvent(GXBaseEvent<R> event) {
        GXCommonUtils.publishEvent(event);
    }

    /**
     * 获取 Primary Key
     *
     * @return String
     */
    default String getPrimaryKey() {
        return "id";
    }
}
