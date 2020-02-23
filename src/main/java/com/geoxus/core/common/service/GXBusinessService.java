package com.geoxus.core.common.service;

import cn.hutool.core.annotation.AnnotationUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.mapper.GXBaseMapper;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.validator.GXValidateDBExists;
import com.geoxus.core.common.validator.GXValidateDBUnique;
import com.geoxus.core.common.vo.GXBusinessStatusCode;
import com.geoxus.core.common.vo.response.GXPagination;
import com.geoxus.core.framework.service.GXBaseService;
import com.geoxus.core.framework.service.GXCoreMediaLibraryService;

import javax.validation.ConstraintValidatorContext;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public interface GXBusinessService<T> extends GXBaseService<T>, GXValidateDBExists, GXValidateDBUnique {
    /**
     * 创建数据
     */
    default long create(T target, Dict param) {
        return 0;
    }

    /**
     * 更新数据
     *
     * @param target
     * @param param
     * @return
     */
    default long update(T target, Dict param) {
        return 0;
    }

    /**
     * 删除数据
     */
    default boolean delete(Dict param) {
        return batchDelete(param);
    }

    /**
     * 列表或者搜索
     */
    default GXPagination listOrSearchPage(Dict param) {
        return new GXPagination(Collections.emptyList());
    }

    /**
     * 列表或者搜索
     */
    default List<Dict> listOrSearch(Dict param) {
        return Collections.emptyList();
    }

    /**
     * 内容详情
     */
    default Dict detail(Dict param) {
        return Dict.create();
    }

    /**
     * 批量删除
     *
     * @param param
     * @return
     */
    default boolean batchDelete(Dict param) {
        final List<Long> ids = Convert.convert(new TypeReference<List<Long>>() {
        }, param.getObj(getPrimaryKey()));
        if (null == ids) {
            return false;
        }
        final ArrayList<T> updateEntities = new ArrayList<>();
        for (Long id : ids) {
            T entity = getById(id);
            final Object status = ReflectUtil.invoke(entity, "getStatus");
            Long entityCurrentStatus = 0L;
            if (null != status) {
                entityCurrentStatus = Convert.toLong(status, 0L);
            }
            if ((entityCurrentStatus & GXBusinessStatusCode.DELETED.getCode()) != GXBusinessStatusCode.DELETED.getCode()) {
                ReflectUtil.invoke(entity, "setStatus", GXBusinessStatusCode.DELETED.getCode());
                updateEntities.add(entity);
            }
        }
        return updateEntities.isEmpty() || updateBatchById(updateEntities);
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
     * @param constraintValidatorContext The ValidatorContext
     * @param param                      param
     * @return boolean
     * @throws UnsupportedOperationException
     */
    default boolean validateExists(Object value, String field, ConstraintValidatorContext constraintValidatorContext, Dict param) throws UnsupportedOperationException {
        return null != getById(Convert.toLong(value));
    }

    /**
     * 验证数据的唯一性
     *
     * @param value                      值
     * @param field                      字段名字
     * @param constraintValidatorContext 验证上下文对象
     * @param param                      参数
     * @return boolean
     */
    default boolean validateUnique(Object value, String field, ConstraintValidatorContext constraintValidatorContext, Dict param) {
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
        String modelIdKey = getPrimaryKey();
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
        int currentPage = GXCommonConstants.DEFAULT_CURRENT_PAGE;
        int pageSize = GXCommonConstants.DEFAULT_PAGE_SIZE;
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
        final List<Dict> list = baseMapper.listOrSearchPage(riPage, param);
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
     * 获取记录的父级path
     *
     * @param parentId   父级ID
     * @param appendSelf 　是否将parentId附加到返回结果上面
     * @return String
     */
    default String getParentPath(Class<T> clazz, Long parentId, boolean appendSelf) {
        final Dict dict = getFieldBySQL(clazz, CollUtil.newHashSet("path"), Dict.create().set(getPrimaryKey(), parentId));
        if (null == dict || dict.isEmpty()) {
            return "0";
        }
        if (appendSelf) {
            return StrUtil.format("{}-{}", dict.getStr("path"), parentId);
        }
        return dict.getStr("path");
    }
}
