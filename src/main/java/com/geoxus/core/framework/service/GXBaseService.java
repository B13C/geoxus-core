package com.geoxus.core.framework.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.event.GXBaseEvent;
import com.geoxus.core.common.event.GXMediaLibraryEvent;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.mapper.GXBaseMapper;
import com.geoxus.core.common.util.GXCommonUtils;
import com.geoxus.core.common.util.GXHttpContextUtils;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.util.GXSyncEventBusCenterUtils;
import com.geoxus.core.framework.entity.GXCoreMediaLibraryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.SQLException;
import java.util.*;

/**
 * 业务基础Service
 *
 * @param <T>
 * @author britton chen
 * @email <britton@126.com>
 */
public interface GXBaseService<T> extends IService<T> {
    @GXFieldCommentAnnotation(zh = "日志对象")
    Logger log = LoggerFactory.getLogger(GXBaseService.class);

    /**
     * 获取当前接口的常量字段信息
     *
     * @return
     */
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
     *
     * @param entity
     * @param path
     * @return
     * @example {
     * "entity":GoodsEntity,
     * "path":"ext.name",
     * "type":Integer.class
     * }
     */
    default <R> R getSingleJSONFieldValue(T entity, String path, Class<R> type) {
        JSON json = JSONUtil.parse(JSONUtil.toJsonStr(entity));
        int index = StrUtil.indexOf(path, '.');
        if (index == -1) {
            if (null == json.getByPath(path)) {
                return GXCommonUtils.getClassDefaultValue(type);
            }
            return Convert.convert(type, json.getByPath(path));
        }
        String mainField = StrUtil.sub(path, 0, index);
        if (null == json.getByPath(mainField)) {
            throw new GXException(StrUtil.format("实体的{}字段不存在!", mainField));
        }
        String subField = StrUtil.sub(path, index + 1, path.length());
        JSON parse = JSONUtil.parse(json.getByPath(mainField));
        if (null == parse.getByPath(subField, type)) {
            return GXCommonUtils.getClassDefaultValue(type);
        }
        return Convert.convert(type, parse.getByPath(subField));
    }

    /**
     * 获取实体中指定指定的值
     *
     * @param entity
     * @param path
     * @param defaultValue
     * @return
     * @example {
     * "entity":GoodsEntity,
     * "path":"ext.name",
     * "type":Integer.class
     * "defaultValue":0
     * }
     */
    default <R> R getSingleJSONFieldValue(T entity, String path, Class<R> type, R defaultValue) {
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
     * @param entity
     * @param dict
     * @return
     */
    default Dict getMultiJSONFieldValue(T entity, Dict dict) {
        final Set<String> keySet = dict.keySet();
        final Dict data = Dict.create();
        for (String key : keySet) {
            final Object value = getSingleJSONFieldValue(entity, key, (Class<?>) dict.getObj(key));
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
     * @return
     */
    default Collection<GXCoreMediaLibraryEntity> getMedia(int modelId, int coreModelId, Dict param) {
        final GXCoreMediaLibraryService mediaLibraryService = GXSpringContextUtils.getBean(GXCoreMediaLibraryService.class);
        return mediaLibraryService.listByMap(param.set("model_id", modelId).set("core_model_id", coreModelId));
    }

    /**
     * 更新实体字段,不能更新虚拟字段
     *
     * @param condition
     * @param fieldName
     * @param value
     * @return
     */
    default boolean updateFieldByCondition(Dict condition, String fieldName, Object value) {
        final T target = getOne(new QueryWrapper<T>().allEq(condition));
        if (null == target) {
            return false;
        }
        try {
            final Field field = target.getClass().getDeclaredField(StringUtils.underlineToCamel(fieldName));
            field.setAccessible(true);
            field.set(target, value);
            return updateById(target);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("updateFieldByCondition", e);
        }
        return false;
    }

    /**
     * 更新JSON字段中的某一个值
     *
     * @param clazz
     * @param path
     * @param value
     * @param condition
     * @return
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
     * 获取模型指定字段
     *
     * @param condition
     * @param fieldName
     * @return
     */
    default Object getEntitySingleField(Dict condition, String fieldName) {
        final T t = getOne(new QueryWrapper<T>().allEq(condition));
        final Dict dict = Dict.parse(t);
        return dict.get(fieldName);
    }

    /**
     * 获取模型指定字段
     *
     * @param condition
     * @param fieldName
     * @return
     */
    default <R> R getEntitySingleField(Dict condition, String fieldName, Class<R> type, R defaultValue) {
        final T t = getOne(new QueryWrapper<T>().allEq(condition));
        if (null == t) {
            return defaultValue;
        }
        final Dict dict = Dict.parse(t);
        return Convert.convert(type, dict.get(fieldName), defaultValue);
    }

    /**
     * 获取模型指定字段
     *
     * @param condition
     * @param fieldNames
     * @return
     */
    default Dict getEntityMultiFields(Dict condition, String... fieldNames) {
        final T t = getOne(new QueryWrapper<T>().allEq(condition));
        final Dict beanDict = Dict.parse(t);
        final Dict dict = Dict.create();
        for (String key : fieldNames) {
            dict.set(key, beanDict.get(key));
        }
        return dict;
    }

    /**
     * 处理用户上传的资源文件
     *
     * @param target
     * @param modelId
     * @param param
     */
    default void handleMedia(T target, long modelId, @NotNull Dict param) {
        final List media = GXHttpContextUtils.getHttpParam("media_info", List.class);
        if (null != media) {
            param.set("media", media);
            param.set("model_id", modelId);
            final GXMediaLibraryEvent<T> event = new GXMediaLibraryEvent<>(target, param);
            GXSyncEventBusCenterUtils.getInstance().post(event);
        }
    }

    /**
     * 根据条件获取一条记录
     *
     * @param condition
     * @return
     */
    default Dict getOneByCondition(Class<T> clazz, Set<String> fieldSet, Dict condition) {
        return getFieldBySQL(clazz, fieldSet, condition);
    }

    /**
     * 检测给定的条件记录是否存在
     *
     * @param clazz     实体的Class
     * @param condition 条件
     * @return
     */
    default Integer checkRecordIsExists(Class<T> clazz, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.checkRecordIsExists(getTableName(clazz), condition);
    }

    /**
     * 获取实体的表明
     *
     * @param clazz
     * @return
     */
    default String getTableName(Class<T> clazz) {
        return GXCommonUtils.getTableName(clazz);
    }

    /**
     * 修改状态
     *
     * @param status    状态
     * @param condition 条件
     * @param operator  操作
     * @return
     */
    default boolean modifyStatus(int status, Dict condition, String operator) {
        final Type type = ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[1];
        Class<T> clazz = Convert.convert(new TypeReference<Class<T>>() {
        }, type);
        return updateStatusBySQL(clazz, status, condition, operator);
    }

    /**
     * 通过SQL更新表中的数据
     *
     * @param clazz
     * @param data
     * @param condition
     * @return
     */
    default boolean updateFieldBySQL(Class<T> clazz, Dict data, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.updateFieldByCondition(getTableName(clazz), data, condition);
    }

    /**
     * 通过SQL更新表中的数据
     *
     * @param clazz
     * @param status
     * @param condition
     * @return
     */
    default boolean updateStatusBySQL(Class<T> clazz, int status, Dict condition, String operator) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.updateStatusByCondition(getTableName(clazz), status, condition, operator);
    }

    /**
     * 通过SQL语句批量插入数据
     *
     * @param clazz    实体的Class
     * @param fieldSet 字段集合
     * @param dataList 数据集合
     * @return
     */
    default Integer batchInsertBySQL(Class<T> clazz, Set<String> fieldSet, List<Dict> dataList) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.batchInsertBySQL(getTableName(clazz), fieldSet, dataList);
    }

    /**
     * 获取表中的指定字段
     *
     * @param clazz
     * @param fieldSet
     * @param condition
     * @return
     */
    default Dict getFieldBySQL(Class<T> clazz, Set<String> fieldSet, Dict condition) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.getFieldBySQL(getTableName(clazz), fieldSet, condition);
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
    @Transactional(rollbackFor = Exception.class)
    default boolean recordModificationHistory(String originTableName, String historyTableName, Dict condition, Dict appendData) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        assert baseMapper != null;
        final Dict targetDict = baseMapper.getFieldBySQL(originTableName, CollUtil.newHashSet("*"), condition);
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
     * @param event
     */
    default void postSyncEvent(GXBaseEvent<T> event) {
        GXCommonUtils.postSyncEvent(event);
    }

    /**
     * 派发异步事件
     *
     * @param event
     */
    default void postAsyncEvent(GXBaseEvent<T> event) {
        GXCommonUtils.postAsyncEvent(event);
    }

    /**
     * 记录日志信息到数据库
     * 不同类别的日志信心记录到不同的数据库中
     *
     * @return
     */
    default Dict logInfoToDB(Dict param) {
        return Dict.create();
    }

    /**
     * 获取 Primary Key
     *
     * @return
     */
    default String getPrimaryKey() {
        return "id";
    }
}
