package com.geoxus.core.framework.service;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.IService;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.event.GXMediaLibraryEvent;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.mapper.GXBaseMapper;
import com.geoxus.core.common.util.GXCommonUtils;
import com.geoxus.core.common.util.GXHttpContextUtils;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.util.GXSyncEventBusCenterUtils;
import com.geoxus.core.common.validator.impl.GXValidatorUtils;
import com.geoxus.core.framework.entity.GXCoreMediaLibraryEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.NotNull;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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
     * 标识核心模型主键名字
     *
     * @return
     */
    default String getCoreModelPrimaryName() {
        return "core_model_id";
    }

    /**
     * 获取模型标识
     *
     * @return
     */
    default String getModelIdentification() {
        return "";
    }

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
     * @param entity
     * @param path
     * @param value
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateJSONFieldSingleValue(T entity, String path, Object value) {
        final T bean = modifyEntityJSONFieldSingleValue(entity, path, value);
        return updateById(bean);
    }

    /**
     * 更新JSON字段中的某多个值
     *
     * @param entity
     * @param param
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    default boolean updateJSONFieldMultiValue(T entity, Dict param) {
        final T bean = modifyEntityJSONFieldMultiValue(entity, param);
        return updateById(bean);
    }

    /**
     * 修改实体的JSON字段的值
     *
     * @param entity
     * @param path
     * @param value
     * @return
     */
    default T modifyEntityJSONFieldSingleValue(T entity, String path, Object value) {
        if (ObjectUtil.isEmpty(value)) {
            return entity;
        }
        JSONObject jsonObject = JSONUtil.parseObj(JSONUtil.toJsonStr(entity));
        final boolean deleteFlag = StrUtil.startWith(path, "-");
        if (deleteFlag) {
            path = StrUtil.sub(path, 1, path.length());
        }
        int index = StrUtil.indexOf(path, '.');
        if (index == -1) {
            GXCommonUtils.putDataToJSONStr(jsonObject, path, value, true);
        } else {
            String mainPath = StrUtil.sub(path, 0, index);
            String subPath = StrUtil.sub(path, index + 1, path.length());
            JSONObject o = jsonObject.getByPath(mainPath, JSONObject.class);
            if (null != o && deleteFlag) {
                GXCommonUtils.removeJSONObjectAnyPath(o, subPath);
                jsonObject.putByPath(mainPath, o);
            } else {
                GXCommonUtils.putDataToJSONStr(jsonObject, path, value, true);
            }
        }
        final T bean = (T) JSONUtil.toBean((JSONObject) jsonObject, entity.getClass());
        if (null != ReflectUtil.getFieldValue(entity, "enableValidateEntity")
                && Convert.convert(Boolean.class, ReflectUtil.getFieldValue(entity, "enableValidateEntity"))) {
            GXValidatorUtils.validateEntity(bean);
        }
        return bean;
    }

    /**
     * 修改实体的JSON字段的值
     *
     * @param entity
     * @param param
     * @return
     */
    default T modifyEntityJSONFieldMultiValue(T entity, Dict param) {
        JSONObject jsonObject = JSONUtil.parseObj(entity);
        final Set<String> mainFieldKeySet = param.keySet();
        for (String mainPath : mainFieldKeySet) {
            final Dict subDict = Convert.convert(Dict.class, param.getObj(mainPath));
            final Set<String> subFieldKeySet = subDict.keySet();
            JSONObject o = jsonObject.getByPath(mainPath, JSONObject.class);
            for (String subPath : subFieldKeySet) {
                final boolean deleteFlag = StrUtil.startWith(subPath, "-");
                final Object value = subDict.getObj(subPath);
                if (deleteFlag) {
                    subPath = StrUtil.sub(subPath, 1, subPath.length());
                }
                if (deleteFlag || ObjectUtil.isEmpty(value)) {
                    GXCommonUtils.removeJSONObjectAnyPath(o, subPath);
                } else {
                    GXCommonUtils.putDataToJSONStr(o, subPath, value, true);
                }
            }
            jsonObject.putByPath(mainPath, o);
        }
        final T bean = (T) JSONUtil.toBean((JSONObject) jsonObject, entity.getClass());
        if (null != ReflectUtil.getFieldValue(entity, "enableValidateEntity")
                && Convert.convert(Boolean.class, ReflectUtil.getFieldValue(entity, "enableValidateEntity"))) {
            GXValidatorUtils.validateEntity(bean);
        }
        return bean;
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
        final TableName annotation = AnnotationUtil.getAnnotation(clazz, TableName.class);
        if (null != annotation) {
            return annotation.value();
        }
        return "";
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
     * 更新实体JSON的多个字段
     *
     * @param target
     * @param param
     * @return
     */
    default boolean updateJSONMultiFields(T target, List<Dict> param) {
        JSON json = JSONUtil.parse(JSONUtil.toJsonStr(target));
        for (Dict info : param) {
            json.putByPath(info.getStr("path"), info.getObj("value"));
        }
        final T bean = (T) JSONUtil.toBean((JSONObject) json, target.getClass());
        return updateById(bean);
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
