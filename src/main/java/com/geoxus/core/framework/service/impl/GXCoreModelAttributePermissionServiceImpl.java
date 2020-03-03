package com.geoxus.core.framework.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.framework.entity.GXCoreModelAttributesPermissionEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesPermissionMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributePermissionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GXCoreModelAttributePermissionServiceImpl extends ServiceImpl<GXCoreModelAttributesPermissionMapper, GXCoreModelAttributesPermissionEntity> implements GXCoreModelAttributePermissionService {
    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #coreModelId")
    public List<String> getModelAttributePermissionByCoreModelId(int coreModelId) {
        final List<Dict> attributes = baseMapper.getModelAttributePermissionByModelId(Dict.create().set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId));
        final ArrayList<String> strings = new ArrayList<>();
        for (Dict dict : attributes) {
            final String modelAttributeField = dict.getStr("model_attribute_field");
            final String attributeName = dict.getStr("attribute_name");
            if (StrUtil.isNotBlank(modelAttributeField) && StrUtil.isNotEmpty(modelAttributeField) && StrUtil.isNotBlank(attributeName)) {
                strings.add(StrUtil.format("{}->>'$.{}'", modelAttributeField, attributeName));
            } else {
                strings.add(dict.getStr("field_name"));
            }
        }
        return strings;
    }
}
