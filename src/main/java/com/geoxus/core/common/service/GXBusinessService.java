package com.geoxus.core.common.service;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoxus.core.common.mapper.GXBaseMapper;
import com.geoxus.core.common.vo.GXBusinessStatusCode;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.validator.GXValidateDBExists;
import com.geoxus.core.common.vo.response.GXPagination;
import com.geoxus.core.framework.service.GXCoreMediaLibraryService;
import com.geoxus.core.framework.service.GXBaseService;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface GXBusinessService<T> extends GXBaseService<T>, GXValidateDBExists {
    /**
     * 核心模型的字段名字
     */
    String CORE_MODEL_ID_NAME = "core_model_id";

    /**
     * 日志核心模型ID
     */
    int CORE_MODEL_ID = 0;

    /**
     * 创建数据
     */
    long create(T target, Dict param);

    /**
     * 更新数据
     *
     * @param target
     * @param param
     * @return
     */
    long update(T target, Dict param);

    /**
     * 删除数据
     */
    default boolean delete(Dict param) {
        return batchDelete(param);
    }

    /**
     * 列表或者搜索
     */
    GXPagination listOrSearch(Dict param);

    /**
     * 内容详情
     */
    Dict detail(Dict param);

    /**
     * 批量删除
     *
     * @param param
     * @return
     */
    default boolean batchDelete(Dict param) {
        final List<Long> ids = Optional.ofNullable(Convert.convert(new TypeReference<List<Long>>() {
        }, param.getObj(PRIMARY_KEY))).orElse(new ArrayList<>());
        final ArrayList<T> updateMessageEntities = new ArrayList<>();
        for (Long id : ids) {
            T entity = getById(id);
            entity = modifyEntityJSONFieldSingleValue(entity, "ext.status", GXBusinessStatusCode.DELETED.getCode());
            updateMessageEntities.add(entity);
        }
        return updateMessageEntities.isEmpty() || updateBatchById(updateMessageEntities);
    }

    /**
     * 修改状态
     *
     * @param condition
     * @param status
     * @return
     */
    default boolean modifyStatus(Class<T> clazz, Dict condition, int status) {
        GXBaseMapper<T> baseMapper = (GXBaseMapper<T>) getBaseMapper();
        return baseMapper.updateFieldByCondition(getTableName(clazz), Dict.create().set("status", status), condition);
    }

    /**
     * 修改状态
     *
     * @param condition
     * @param status
     * @return
     */
    default boolean modifyStatus(Dict condition, int status) {
        final T entity = getOne(new QueryWrapper<T>().allEq(condition));
        return updateJSONFieldSingleValue(entity, "ext.status", status);
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
     * 通过条件获取配置信息
     *
     * @param condition
     * @return
     */
    default Dict getDataByCondition(Dict condition) {
        final T entity = getOne(new QueryWrapper<T>().allEq(condition));
        return (null != entity) ? Dict.parse(entity) : Dict.create();
    }

    /**
     * 通过条件获取配置信息
     *
     * @param condition
     * @return
     */
    default Dict getDataByCondition(Dict condition, String... fields) {
        final T entity = getOne(new QueryWrapper<T>().select(fields).allEq(condition));
        return (null != entity) ? Dict.parse(entity) : Dict.create();
    }

    /**
     * 实现验证注解
     *
     * @param value                      The value to check for
     * @param field                      The name of the field for which to check if the value exists
     * @param constraintValidatorContext
     * @param param
     * @return
     * @throws UnsupportedOperationException
     */
    default boolean validateExists(Object value, String field, ConstraintValidatorContext constraintValidatorContext, Dict param) throws UnsupportedOperationException {
        return null != getById(Convert.toLong(value));
    }

    /**
     * 合并分页数据中的每条数据的资源文件
     * <pre>
     *     {@code
     *     mergePaginationCoreMediaLibrary(pagination,"bannerId")
     *     }
     * </pre>
     *
     * @param pagination 分页数据
     * @param modelIdKey 分页数据中模型的key,一般为数据表主键名字的驼峰名字
     * @return
     */
    default GXPagination mergePaginationCoreMediaLibrary(GXPagination pagination, String modelIdKey) {
        final GXCoreMediaLibraryService coreMediaLibraryService = GXSpringContextUtils.getBean(GXCoreMediaLibraryService.class);
        final List<?> records = pagination.getRecords();
        for (int i = 0; i < records.size(); i++) {
            final Dict o = (Dict) records.get(i);
            o.set("media", coreMediaLibraryService.getMediaByCondition(Dict.create().set("model_id", o.getLong(modelIdKey)).set("core_model_id", o.getLong("coreModelId"))));
        }
        return pagination;
    }

    /**
     * 合并分页数据中的每条数据的资源文件
     * <pre>
     *     {@code
     *     mergePaginationCoreMediaLibrary(pagination)
     *     }
     * </pre>
     *
     * @param pagination 分页数据
     * @return
     */
    default GXPagination mergePaginationCoreMediaLibrary(GXPagination pagination) {
        String modelIdKey = getPrimaryKey(true);
        final GXCoreMediaLibraryService coreMediaLibraryService = GXSpringContextUtils.getBean(GXCoreMediaLibraryService.class);
        final List<?> records = pagination.getRecords();
        for (int i = 0; i < records.size(); i++) {
            final Dict o = (Dict) records.get(i);
            o.set("media", coreMediaLibraryService.getMediaByCondition(Dict.create().set("model_id", o.getLong(modelIdKey)).set("core_model_id", o.getLong("coreModelId"))));
        }
        return pagination;
    }

    /**
     * 获取分页对象信息
     *
     * @param param
     * @return
     */
    default IPage<Dict> constructPageObjectFromParam(Dict param) {
        final Dict pageInfo = getPageInfoFromParam(param);
        return new Page<>(pageInfo.getInt("current"), pageInfo.getInt("size"));
    }

    /**
     * 从请求参数中获取分页的信息
     *
     * @param param
     * @return
     */
    default Dict getPageInfoFromParam(Dict param) {
        int currentPage = DEFAULT_CURRENT_PAGE;
        int pageSize = DEFAULT_PAGE_SIZE;
        final Dict pagingInfo = Convert.convert(Dict.class, param.getObj("paging_info"));
        if (null != pagingInfo) {
            if (null != pagingInfo.getInt("current")) {
                currentPage = pagingInfo.getInt("current");
            }
            if (null != pagingInfo.getInt("size")) {
                pageSize = pagingInfo.getInt("size");
            }
        }
        return Dict.create().set("current", currentPage).set("size", pageSize);
    }

    /**
     * 获取分页信息
     *
     * @param param
     * @return
     */
    default GXPagination generatePage(Dict param) {
        final IPage<Dict> riPage = constructPageObjectFromParam(param);
        GXBaseMapper<Dict> baseMapper = (GXBaseMapper<Dict>) getBaseMapper();
        final List<Dict> list = baseMapper.listOrSearch(riPage, param);
        riPage.setRecords(list);
        return new GXPagination(riPage.getRecords(), riPage.getTotal(), riPage.getSize(), riPage.getCurrent());
    }

    /**
     * 将IPage信息转换成Pagination对象
     *
     * @param iPage
     * @param <R>
     * @return
     */
    default <R> GXPagination generatePage(IPage<R> iPage) {
        return new GXPagination(iPage.getRecords(), iPage.getTotal(), iPage.getSize(), iPage.getCurrent());
    }

    /**
     * 分页  返回Dict对象
     *
     * <pre>
     *     {@code
     *     final Dict param = Dict.create().set("paging_info", Dict.create().set("current", 1).set("size", 2));
     *     IPage<Dict> iPage = generatePage(param , "BannerMapper.listOrSearch",Dict.class);
     *     }
     * </pre>
     *
     * @param param
     * @param mapperMethodName
     * @return
     */
    default GXPagination generatePage(Dict param, String mapperMethodName) {
        final IPage<Dict> iPage = constructPageObjectFromParam(param);
        final List<Dict> list = ReflectUtil.invoke(getBaseMapper(), mapperMethodName, iPage, param);
        iPage.setRecords(list);
        return new GXPagination(iPage.getRecords(), iPage.getTotal(), iPage.getSize(), iPage.getCurrent());
    }

    /**
     * 分页  返回实体对象
     *
     * @param param
     * @param mapperMethodName
     * @param isEntity
     * @return
     */
    default GXPagination generatePage(Dict param, String mapperMethodName, boolean isEntity) {
        if (!isEntity) {
            return generatePage(param, mapperMethodName);
        }
        final Dict pageParam = getPageInfoFromParam(param);
        final IPage<T> iPage = new Page<>(pageParam.getInt("current"), pageParam.getInt("size"));
        final List<T> list = ReflectUtil.invoke(getBaseMapper(), mapperMethodName, iPage, param);
        iPage.setRecords(list);
        return new GXPagination(iPage.getRecords(), iPage.getTotal(), iPage.getSize(), iPage.getCurrent());
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
}
