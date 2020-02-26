package com.geoxus.core.framework.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXBaseBuilderConstants;
import com.geoxus.core.common.util.GXCacheKeysUtils;
import com.geoxus.core.common.util.GXCommonUtils;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;
import com.geoxus.core.framework.entity.GXCoreModelEntity;
import com.geoxus.core.framework.mapper.GXCoreModelMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributesService;
import com.geoxus.core.framework.service.GXCoreModelService;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GXCoreModelServiceImpl extends ServiceImpl<GXCoreModelMapper, GXCoreModelEntity> implements GXCoreModelService {
    @GXFieldCommentAnnotation(zh = "获取Guava的缓存组件")
    private static final Cache<String, GXCoreModelEntity> cache;

    static {
        cache = GXCommonUtils.getGuavaCache(10000, 24, TimeUnit.HOURS, false);
    }

    @Autowired
    private GXCacheKeysUtils gxCacheKeysUtils;

    @Autowired
    private GXCoreModelAttributesService coreModelAttributeService;

    @Override
    @Cacheable(value = "core_model", key = "targetClass + methodName + #modelId + #subField")
    public GXCoreModelEntity getCoreModelByModelId(int modelId, String subField) {
        final GXCoreModelEntity entity = getById(modelId);
        if (null == entity) {
            return null;
        }
        if (StrUtil.isBlank(subField)) {
            subField = null;
        }
        final List<GXCoreModelAttributesEntity> attributes = coreModelAttributeService.getModelAttributesByModelId(Dict.create().set("model_id", modelId).set("model_attribute_field", subField));
        entity.setCoreAttributesEntities(attributes);
        return entity;
    }

    @Override
    public boolean checkModelHasAttribute(int modelId, String attributeName) {
        return null != coreModelAttributeService.checkCoreModelHasAttribute(modelId, attributeName);
    }

    @Override
    public boolean checkFormKeyMatch(Set<String> keySet, String modelName) {
        final GXCoreModelEntity modelEntity = getCoreModelByModelId(getModelIdByModelIdentification(modelName), null);
        if (null == modelEntity) {
            return false;
        }
        final List<GXCoreModelAttributesEntity> attributesEntities = modelEntity.getCoreAttributesEntities();
        final Set<String> keys = new HashSet<>();
        for (GXCoreModelAttributesEntity attribute : attributesEntities) {
            keys.add(attribute.getFieldName());
        }
        return keys.retainAll(keySet);
    }

    @Override
    public int getModelIdByModelIdentification(String modelName) {
        final String cacheKey = gxCacheKeysUtils.getCacheKey("", "core_model_" + modelName);
        final GXCoreModelEntity o = getCacheValueFromLoader(cache, cacheKey, () -> {
            log.info("getModelIdByModelIdentification() From DB Get Data!");
            return getOne(new QueryWrapper<GXCoreModelEntity>().select("model_id").eq("model_identification", modelName));
        });
        return null == o ? 0 : o.getModelId();
    }

    @Override
    @Cacheable(value = "core_model", key = "targetClass + methodName + #p0")
    public String getModelTypeByModelId(long coreModelId, String defaultValue) {
        final GXCoreModelEntity entity = getOne(new QueryWrapper<GXCoreModelEntity>().select("model_type").eq("model_id", coreModelId));
        if (null == entity) {
            return defaultValue + "Type";
        }
        return entity.getModelType();
    }

    @Override
    public Dict getSearchCondition(Dict condition) {
        final Dict searchCondition = baseMapper.getSearchCondition(condition);
        if (null == searchCondition) {
            return Dict.create();
        }
        return Convert.convert(new TypeReference<Dict>() {
        }, JSONUtil.parse(Optional.ofNullable(searchCondition.getObj(GXBaseBuilderConstants.SEARCH_CONDITION_NAME)).orElse("{}")));
    }

    @Override
    public boolean validateExists(Object value, String field, ConstraintValidatorContext constraintValidatorContext, Dict param) throws UnsupportedOperationException {
        log.info("validateExists : {} , field : {}", value, field);
        final Integer coreModelId = Convert.toInt(value);
        return coreModelId == null || null != getById(coreModelId);
    }
}
