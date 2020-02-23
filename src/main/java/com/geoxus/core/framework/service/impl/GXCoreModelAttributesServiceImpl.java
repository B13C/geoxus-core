package com.geoxus.core.framework.service.impl;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributesService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GXCoreModelAttributesServiceImpl extends ServiceImpl<GXCoreModelAttributesMapper, GXCoreModelAttributesEntity> implements GXCoreModelAttributesService {
    @Override
    public List<GXCoreModelAttributesEntity> getModelAttributesByModelId(Dict param) {
        return baseMapper.getModelAttributesByModelId(param);
    }

    @Override
    @Cacheable(value = "attribute_group", key = "targetClass + methodName + #modelId + #attributeId")
    public GXCoreModelAttributesEntity getModelAttributeByModelIdAndAttributeId(int modelId, int attributeId) {
        final Dict condition = Dict.create().set("model_id", modelId).set("attribute_id", attributeId);
        return getOne(new QueryWrapper<GXCoreModelAttributesEntity>().allEq(condition));
    }

    public Integer checkCoreModelHasAttribute(Integer coreModelId, String attributeName) {
        final Dict condition = Dict.create().set("core_model_id", coreModelId).set("field_name", attributeName);
        return baseMapper.checkCoreModelHasAttribute(condition);
    }
}
