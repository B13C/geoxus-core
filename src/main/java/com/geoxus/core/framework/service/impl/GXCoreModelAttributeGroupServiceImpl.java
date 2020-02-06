package com.geoxus.core.framework.service.impl;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.framework.entity.GXCoreModelAttributeGroupEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributeGroupMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributeGroupService;
import org.mybatis.dynamic.sql.select.render.SelectStatementProvider;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GXCoreModelAttributeGroupServiceImpl extends ServiceImpl<GXCoreModelAttributeGroupMapper, GXCoreModelAttributeGroupEntity> implements GXCoreModelAttributeGroupService {
    @Override
    public List<GXCoreModelAttributeGroupEntity> getModelAttributeByModelId(SelectStatementProvider selectStatementProvider) {
        return baseMapper.getModelAttributeByModelId(selectStatementProvider);
    }

    @Override
    @Cacheable(value = "attribute_group", key = "targetClass + methodName + #modelId + #attributeId")
    public GXCoreModelAttributeGroupEntity getAttributeGroupByAttributeIdAndModelId(int modelId, int attributeId) {
        final Dict condition = Dict.create().set("model_id", modelId).set("attribute_id", attributeId);
        return getOne(new QueryWrapper<GXCoreModelAttributeGroupEntity>().allEq(condition));
    }
}
