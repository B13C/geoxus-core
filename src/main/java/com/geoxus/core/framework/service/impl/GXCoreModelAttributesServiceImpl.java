package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.util.GXCacheKeysUtils;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributesService;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@Slf4j
public class GXCoreModelAttributesServiceImpl extends ServiceImpl<GXCoreModelAttributesMapper, GXCoreModelAttributesEntity> implements GXCoreModelAttributesService {
    @GXFieldCommentAnnotation(zh = "Guava缓存组件")
    @Autowired
    private Cache<String, Dict> coreModelAttributesDictCache;

    @GXFieldCommentAnnotation(zh = "通用缓存对象")
    @Autowired
    private Cache<String, Object> generalGuavaCache;

    @Autowired
    private GXCacheKeysUtils gxCacheKeysUtils;

    @Override
    public List<GXCoreModelAttributesEntity> getModelAttributesByModelId(Dict param) {
        final String cacheKey = gxCacheKeysUtils.getCacheKey("", "model_attributes_list_" + param.getInt("model_id"));
        final Object o = getCacheValueFromLoader(generalGuavaCache, cacheKey, () -> {
            log.info("getModelAttributesByModelId() From DB Get Data!");
            return baseMapper.getModelAttributesByModelId(param);
        });
        return Convert.convert(new TypeReference<List<GXCoreModelAttributesEntity>>() {
        }, o);
    }

    @Override
    @Cacheable(value = "attribute_group", key = "targetClass + methodName + #modelId + #attributeId")
    public Dict getModelAttributeByModelIdAndAttributeId(int modelId, int attributeId) {
        final Dict condition = Dict.create().set("model_id", modelId).set("attribute_id", attributeId);
        final HashSet<String> fieldSet = CollUtil.newHashSet("validation_expression", "force_validation", "required");
        final String cacheKey = gxCacheKeysUtils.getCacheKey("", StrUtil.format("core:model:attributes:{}:{}", modelId, attributeId));
        return getCacheValueFromLoader(coreModelAttributesDictCache, cacheKey, () -> {
            log.info("getModelAttributeByModelIdAndAttributeId() From DB Get Data!");
            return getFieldBySQL(GXCoreModelAttributesEntity.class, fieldSet, condition);
        });
    }

    public Integer checkCoreModelHasAttribute(Integer coreModelId, String attributeName) {
        final Dict condition = Dict.create().set("core_model_id", coreModelId).set("attribute_name", attributeName);
        final String cacheKey = gxCacheKeysUtils.getCacheKey("", StrUtil.format("core:model:attributes:{}:{}", coreModelId, attributeName));
        final Object value = getCacheValueFromLoader(generalGuavaCache, cacheKey, () -> {
            log.info("checkCoreModelHasAttribute() From DB Get Data!");
            return baseMapper.checkCoreModelHasAttribute(condition);
        });
        return (Integer) value;
    }
}
