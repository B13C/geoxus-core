package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.constant.GXBaseBuilderConstants;
import com.geoxus.core.framework.entity.GXCoreModelEntity;
import com.geoxus.core.framework.mapper.GXCoreModelMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributesService;
import com.geoxus.core.framework.service.GXCoreModelService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class GXCoreModelServiceImpl extends ServiceImpl<GXCoreModelMapper, GXCoreModelEntity> implements GXCoreModelService {
    @Autowired
    private GXCoreModelAttributesService coreModelAttributeService;

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #modelId + #subField")
    public GXCoreModelEntity getCoreModelByModelId(int modelId, String subField) {
        final GXCoreModelEntity entity = getById(modelId);
        if (null == entity) {
            return null;
        }
        if (StrUtil.isBlank(subField)) {
            subField = null;
        }
        final List<Dict> attributes = coreModelAttributeService.getModelAttributesByModelId(Dict.create().set("model_id", modelId).set("model_attribute_field", subField));
        entity.setCoreAttributes(attributes);
        return entity;
    }

    @Override
    public boolean checkModelHasAttribute(int modelId, String attributeName) {
        return 1 == coreModelAttributeService.checkCoreModelHasAttribute(modelId, attributeName);
    }

    @Override
    public boolean checkFormKeyMatch(Set<String> keySet, String modelName) {
        final GXCoreModelEntity modelEntity = getCoreModelByModelId(getModelIdByModelIdentification(modelName), null);
        if (null == modelEntity) {
            return false;
        }
        final List<Dict> attributes = modelEntity.getCoreAttributes();
        final Set<String> keys = new HashSet<>();
        for (Dict dict : attributes) {
            keys.add(dict.getStr("field_name"));
        }
        return keys.retainAll(keySet);
    }

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #p0")
    public Integer getModelIdByModelIdentification(String modelName) {
        final Dict condition = Dict.create().set(GXBaseBuilderConstants.MODEL_IDENTIFICATION_NAME, modelName);
        final Dict dict = getFieldBySQL(GXCoreModelEntity.class, CollUtil.newHashSet("model_id"), condition);
        return null == dict ? 0 : dict.getInt("model_id");
    }

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #coreModelId")
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
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #value + #field")
    public boolean validateExists(Object value, String field, ConstraintValidatorContext constraintValidatorContext, Dict param) throws UnsupportedOperationException {
        log.info("validateExists : {} , field : {}", value, field);
        final Integer coreModelId = Convert.toInt(value, 0);
        final Object o = checkRecordIsExists(GXCoreModelEntity.class, Dict.create().set("model_id", coreModelId));
        return 1 == Convert.convert(Integer.class, o, 0);
    }
}
