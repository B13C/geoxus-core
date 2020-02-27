package com.geoxus.core.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.util.GXCacheKeysUtils;
import com.geoxus.core.framework.entity.GXCoreAttributesEntity;
import com.geoxus.core.framework.mapper.GXCoreAttributesMapper;
import com.geoxus.core.framework.service.GXCoreAttributesService;
import com.google.common.cache.Cache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class GXCoreAttributesServiceImpl extends ServiceImpl<GXCoreAttributesMapper, GXCoreAttributesEntity> implements GXCoreAttributesService {
    @Autowired
    private Cache<String, GXCoreAttributesEntity> coreAttributesEntityCache;

    @Autowired
    private GXCacheKeysUtils gxCacheKeysUtils;

    @Override
    @Cacheable(value = "attributes", key = "targetClass + methodName + #p0")
    public List<GXCoreAttributesEntity> getAttributesByCategory(String category) {
        return Optional.ofNullable(list(new QueryWrapper<GXCoreAttributesEntity>().eq("category", category))).orElse(new ArrayList<>());
    }

    @Override
    //@Cacheable(value = "attributes", key = "targetClass + methodName + #p0")
    public GXCoreAttributesEntity getAttributeByAttributeName(String attributeName) {
        final String cacheKey = gxCacheKeysUtils.getCacheKey("", "get_attribute_byAttribute_name_" + attributeName);
        return getCacheValueFromLoader(coreAttributesEntityCache, cacheKey, () -> {
            log.info("getAttributeByAttributeName() Get Data From DB!");
            return getOne(new QueryWrapper<GXCoreAttributesEntity>().eq("attribute_name", attributeName));
        });
    }

    @Override
    public boolean checkFieldIsExists(String attributeName) {
        return getOne(new QueryWrapper<GXCoreAttributesEntity>().eq("attribute_name", attributeName)) != null;
    }
}
