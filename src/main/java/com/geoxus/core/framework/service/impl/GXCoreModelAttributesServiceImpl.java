package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXCommonConstants;
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
    private static final Cache<String, List<Dict>> LIST_DICT_CACHE;

    static {
        LIST_DICT_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofHours(24)).maximumSize(100000).build();
    }

    @Autowired
    private GXCacheKeysUtils gxCacheKeysUtils;

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #p0.getStr('core_model_id') + #p0.getStr('model_attribute_field')")
    public List<Dict> getModelAttributesByModelId(Dict param) {
        if (null == param.getInt(GXCommonConstants.CORE_MODEL_PRIMARY_NAME)) {
            param.set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, 0);
        }
        final List<Dict> attributes = baseMapper.getModelAttributesByModelId(param);
        return attributes;
    }

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #modelId + #attributeId")
    public Dict getModelAttributeByModelIdAndAttributeId(int modelId, int attributeId) {
        final Dict condition = Dict.create().set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, modelId).set("attribute_id", attributeId);
        final HashSet<String> fieldSet = CollUtil.newHashSet("validation_expression", "force_validation", "required");
        return getFieldBySQL(GXCoreModelAttributesEntity.class, fieldSet, condition);
    }

    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #coreModelId + #attributeName")
    public Integer checkCoreModelHasAttribute(Integer coreModelId, String attributeName) {
        final Dict condition = Dict.create().set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId).set("attribute_name", attributeName);
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
        final String cacheKey = gxCacheKeysUtils.getCacheKey("", StrUtil.format("{}.{}.{}", coreModelId, modelAttributeField, String.join(".", paramSet)));
        final Dict condition = Dict.create().set("model_id", coreModelId).set("model_attribute_field", modelAttributeField);
        try {
            final List<Dict> list = LIST_DICT_CACHE.get(cacheKey, () -> baseMapper.listOrSearch(condition));
            if (list.isEmpty()) {
                return false;
            }
            Set<String> dbSet = CollUtil.newHashSet();
            for (Dict dict : list) {
                dbSet.add(dict.getStr("attribute_name"));
            }
            log.info("checkCoreModelFieldAttributes ->> dbSet : {} , paramSet : {}", dbSet, paramSet);
            return dbSet.toString().equals(paramSet.toString());
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }

    @Override
    public Dict getModelAttributesDefaultValue(int coreModelId, String modelAttributeField, String jsonStr) {
        if (!JSONUtil.isJson(jsonStr)) {
            return Dict.create();
        }
        final Dict sourceDict = JSONUtil.toBean(jsonStr, Dict.class);
        final String cacheKey = gxCacheKeysUtils.getCacheKey("", StrUtil.format("{}.{}", coreModelId, modelAttributeField));
        final Dict condition = Dict.create().set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId).set("db_field_name", modelAttributeField);
        try {
            final Dict retDict = Dict.create();
            final List<Dict> list = LIST_DICT_CACHE.get(cacheKey, () -> baseMapper.listOrSearch(condition));
            for (Dict data : list) {
                final String attributeName = data.getStr("attribute_name");
                Object value = sourceDict.getObj(attributeName);
                if (StrUtil.isBlankIfStr(value)) {
                    value = data.getObj("default_value");
                    if (StrUtil.isBlankIfStr(value)) {
                        value = RandomUtil.randomString(5);
                    }
                }
                retDict.set(attributeName, value);
            }
            return retDict;
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
        }
        return Dict.create();
    }
}
