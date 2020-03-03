package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.util.GXCacheKeysUtils;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributesService;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class GXCoreModelAttributesServiceImpl extends ServiceImpl<GXCoreModelAttributesMapper, GXCoreModelAttributesEntity> implements GXCoreModelAttributesService {
    @GXFieldCommentAnnotation(zh = "Guava缓存组件")
    private static final Cache<String, List<Dict>> cache;

    static {
        cache = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofHours(24)).maximumSize(100000).build();
    }

    @Autowired
    private GXCacheKeysUtils gxCacheKeysUtils;

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #p0.getStr('model_id') + #p0.getStr('model_attribute_field')")
    public List<GXCoreModelAttributesEntity> getModelAttributesByModelId(Dict param) {
        return baseMapper.getModelAttributesByModelId(param);
    }

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #modelId + #attributeId")
    public Dict getModelAttributeByModelIdAndAttributeId(int modelId, int attributeId) {
        final Dict condition = Dict.create().set("model_id", modelId).set("attribute_id", attributeId);
        final HashSet<String> fieldSet = CollUtil.newHashSet("validation_expression", "force_validation", "required");
        return getFieldBySQL(GXCoreModelAttributesEntity.class, fieldSet, condition);
    }

    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #coreModelId + #attributeName")
    public Integer checkCoreModelHasAttribute(Integer coreModelId, String attributeName) {
        final Dict condition = Dict.create().set("core_model_id", coreModelId).set("attribute_name", attributeName);
        return baseMapper.checkCoreModelHasAttribute(condition);
    }

    @Override
    public boolean checkCoreModelFieldAttributes(Integer coreModelId, String modelAttributeField, String jsonStr) {
        Set<String> paramSet = CollUtil.newHashSet();
        final Dict paramData = JSONUtil.toBean(jsonStr, Dict.class);
        if (paramData.isEmpty()) {
            return false;
        }
        for (Map.Entry<String, Object> entry : paramData.entrySet()) {
            paramSet.add(entry.getKey());
        }
        final String cacheKey = gxCacheKeysUtils.getCacheKey("", StrUtil.format("{}.{}.{}", coreModelId, modelAttributeField, paramSet.toString()));
        final Dict condition = Dict.create().set("model_id", coreModelId).set("model_attribute_field", modelAttributeField);
        try {
            final List<Dict> list = cache.get(cacheKey, () -> baseMapper.listOrSearch(condition));
            if (list.isEmpty()) {
                return false;
            }
            Set<String> dbSet = CollUtil.newHashSet();
            for (Dict dict : list) {
                if (dict.getInt("force_validation") == 0) {
                    continue;
                }
                dbSet.add(dict.getStr("attribute_name"));
            }
            log.info("checkCoreModelFieldAttributes ->> dbSet : {} , paramSet : {}", dbSet, paramSet);
            return dbSet.toString().equals(paramSet.toString());
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }
}
