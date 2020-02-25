package com.geoxus.core.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.framework.entity.GXCoreAttributesEntity;
import com.geoxus.core.framework.mapper.GXCoreAttributesMapper;
import com.geoxus.core.framework.service.GXCoreAttributesService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GXCoreAttributesServiceImpl extends ServiceImpl<GXCoreAttributesMapper, GXCoreAttributesEntity> implements GXCoreAttributesService {
    @Override
    @Cacheable(value = "attributes", key = "targetClass + methodName + #p0")
    public List<GXCoreAttributesEntity> getAttributesByCategory(String category) {
        return Optional.ofNullable(list(new QueryWrapper<GXCoreAttributesEntity>().eq("category", category))).orElse(new ArrayList<>());
    }

    @Override
    @Cacheable(value = "attributes", key = "targetClass + methodName + #p0")
    public GXCoreAttributesEntity getAttributeByAttributeName(String attributeName) {
        return Optional.ofNullable(getOne(new QueryWrapper<GXCoreAttributesEntity>().eq("attribute_name", attributeName))).orElse(new GXCoreAttributesEntity());
    }

    @Override
    public boolean checkFieldIsExists(String attributeName) {
        return getOne(new QueryWrapper<GXCoreAttributesEntity>().eq("attribute_name", attributeName)) != null;
    }
}
