package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.service.GXSAdminHasRolesService;
import com.geoxus.core.common.util.GXShiroUtils;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.framework.entity.GXCoreModelAttributesPermissionEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesPermissionMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributePermissionService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class GXCoreModelAttributePermissionServiceImpl extends ServiceImpl<GXCoreModelAttributesPermissionMapper, GXCoreModelAttributesPermissionEntity> implements GXCoreModelAttributePermissionService {
    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #coreModelId")
    public Dict getModelAttributePermissionByCoreModelId(int coreModelId, Dict param) {
        final List<Dict> attributes = baseMapper.getModelAttributePermissionByModelId(Dict.create().set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId));
        final Dict data = Dict.create();
        final Dict jsonFieldDict = Dict.create();
        final Dict dbFieldDict = Dict.create();
        List<String> roles = CollUtil.newArrayList();
        final List<String> users = CollUtil.newArrayList();
        final Long currentAdminId = GXShiroUtils.getAdminId();
        if (null != currentAdminId) {
            users.add(currentAdminId.toString());
            final Dict adminRoles = Objects.requireNonNull(GXSpringContextUtils.getBean(GXSAdminHasRolesService.class)).getAdminRoles(currentAdminId);
            roles = adminRoles.keySet().stream().map(r -> Convert.toStr(r, "0")).collect(Collectors.toList());
        }
        for (Dict dict : attributes) {
            final String dbFieldName = dict.getStr("db_field_name");
            final String[] tmpRoles = StrUtil.split(dict.getStr("roles"), ",");
            final String[] tmpUsers = StrUtil.split(dict.getStr("users"), ",");
            if (!CollUtil.containsAny(Arrays.asList(tmpUsers), users) && !CollUtil.containsAny(Arrays.asList(tmpRoles), roles)) {
                continue;
            }
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
