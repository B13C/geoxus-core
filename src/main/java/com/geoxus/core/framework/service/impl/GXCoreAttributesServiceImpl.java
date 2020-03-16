package com.geoxus.core.framework.service.impl;

import cn.hutool.core.lang.Dict;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.framework.entity.GXCoreAttributesEntity;
import com.geoxus.core.framework.mapper.GXCoreAttributesMapper;
import com.geoxus.core.framework.service.GXCoreAttributesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidatorContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GXCoreAttributesServiceImpl extends ServiceImpl<GXCoreAttributesMapper, GXCoreAttributesEntity> implements GXCoreAttributesService {
    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #p0")
    public List<GXCoreAttributesEntity> getAttributesByCategory(String category) {
        return Optional.ofNullable(list(new QueryWrapper<GXCoreAttributesEntity>().eq("category", category))).orElse(new ArrayList<>());
    }

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #attributeName")
    public GXCoreAttributesEntity getAttributeByAttributeName(String attributeName) {
        return getOne(new QueryWrapper<GXCoreAttributesEntity>().eq("attribute_name", attributeName));
    }

    @Override
    public boolean checkFieldIsExists(String attributeName) {
        return getOne(new QueryWrapper<GXCoreAttributesEntity>().eq("attribute_name", attributeName)) != null;
    }

    @Override
    public boolean validateExists(Object value, String field, ConstraintValidatorContext constraintValidatorContext, Dict param) throws UnsupportedOperationException {
        Dict condition = Dict.create().set(field, value);
        return 1 == checkRecordIsExists(GXCoreAttributesEntity.class, condition);
    }
}
