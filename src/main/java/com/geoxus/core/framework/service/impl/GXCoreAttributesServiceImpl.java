package com.geoxus.core.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.framework.entity.CoreAttributesEntity;
import com.geoxus.core.framework.mapper.GXCoreAttributesMapper;
import com.geoxus.core.framework.service.GXCoreAttributesService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class GXCoreAttributesServiceImpl extends ServiceImpl<GXCoreAttributesMapper, CoreAttributesEntity> implements GXCoreAttributesService {
    @Override
    @Cacheable(value = "attributes", key = "targetClass + methodName + #p0")
    public List<CoreAttributesEntity> getAttributesByCategory(String category) {
        return Optional.ofNullable(list(new QueryWrapper<CoreAttributesEntity>().eq("category", category))).orElse(new ArrayList<>());
    }

    @Override
    @Cacheable(value = "attributes", key = "targetClass + methodName + #p0")
    public CoreAttributesEntity getAttributeByFieldName(String fieldName) {
        return Optional.ofNullable(getOne(new QueryWrapper<CoreAttributesEntity>().eq("field_name", fieldName))).orElse(new CoreAttributesEntity());
    }

    @Override
    public boolean checkFieldIsExists(String fieldName) {
        return getOne(new QueryWrapper<CoreAttributesEntity>().eq("field_name", fieldName)) != null;
    }
}
