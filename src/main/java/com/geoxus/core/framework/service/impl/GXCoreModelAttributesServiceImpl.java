package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
public class GXCoreModelAttributesServiceImpl extends ServiceImpl<GXCoreModelAttributesMapper, GXCoreModelAttributesEntity> implements GXCoreModelAttributesService {
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
        final Dict condition = Dict.create().set("model_id", coreModelId).set("model_attribute_field", modelAttributeField);
        final List<Dict> list = baseMapper.listOrSearch(condition);
        Set<String> set = CollUtil.newHashSet();
        Set<String> set1 = CollUtil.newHashSet();
        for (Dict dict : list) {
            set.add(dict.getStr("attribute_name"));
        }
        final Dict dict = JSONUtil.toBean(jsonStr, Dict.class);
        for (Map.Entry<String, Object> entry : dict.entrySet()) {
            set1.add(entry.getKey());
        }
        log.info("checkCoreModelFieldAttributes ->> set : {} , set1 : {}", set, set1);
        return set.toString().equals(set1.toString());
    }
}
