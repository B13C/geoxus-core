package com.geoxus.core.framework.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.framework.entity.GXCoreModelAttributesPermissionEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesPermissionMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributePermissionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GXCoreModelAttributePermissionServiceImpl extends ServiceImpl<GXCoreModelAttributesPermissionMapper, GXCoreModelAttributesPermissionEntity> implements GXCoreModelAttributePermissionService {
    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #coreModelId")
    public Dict getModelAttributePermissionByCoreModelId(int coreModelId, Dict param) {
        final List<Dict> attributes = baseMapper.getModelAttributePermissionByModelId(Dict.create().set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId));
        final Dict data = Dict.create();
        final Dict jsonFieldDict = Dict.create();
        final Dict dbFieldDict = Dict.create();
        for (Dict dict : attributes) {
            final String dbFieldName = dict.getStr("db_field_name");
            if (StrUtil.contains(dbFieldName, "::")) {
                final String[] strings = StrUtil.split(dbFieldName, "::");
                final Dict convertDict = Convert.convert(Dict.class, jsonFieldDict.getOrDefault(strings[0], Dict.create()));
                convertDict.set(StrUtil.format("{}", strings[1]), StrUtil.format("`{}`", String.join("::", strings)));
                jsonFieldDict.set(strings[0], convertDict);
            } else {
                dbFieldDict.set(dbFieldName, dbFieldName);
            }
        }
        return data.set("json_field", jsonFieldDict).set("db_field", dbFieldDict);
    }
}
