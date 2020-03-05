package com.geoxus.core.framework.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.framework.entity.GXCoreModelAttributesPermissionEntity;
import com.geoxus.core.framework.mapper.GXCoreModelAttributesPermissionMapper;
import com.geoxus.core.framework.service.GXCoreModelAttributePermissionService;
import org.bouncycastle.util.Strings;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #coreModelId + #param")
    public Dict getModelAttributePermissionAllow(int coreModelId, Dict param) {
        final String ROLES_KEY = "roles";
        final String USERS_KEY = "users";
        final List<Dict> attributes = baseMapper.getModelAttributePermissionByModelId(Dict.create().set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId));
        final List<Integer> roles = Convert.convert(new TypeReference<List<Integer>>() {
        }, param.getObj(ROLES_KEY));
        final List<Integer> users = Convert.convert(new TypeReference<List<Integer>>() {
        }, param.getObj(USERS_KEY));
        final Dict retDict = Dict.create();
        for (Dict dict : attributes) {
            if (null != dict.getStr("allow")) {
                final Dict allowDict = JSONUtil.toBean(dict.getStr("allow"), Dict.class);
                final String rolesStr = Optional.ofNullable(allowDict.getStr(ROLES_KEY)).orElse("");
                final String usersStr = Optional.ofNullable(allowDict.getStr(USERS_KEY)).orElse("");
                final List<Integer> allowRoles = Convert.convert(new TypeReference<List<Integer>>() {
                }, Strings.split(rolesStr, ','));
                final List<Integer> allowUsers = Convert.convert(new TypeReference<List<Integer>>() {
                }, Strings.split(usersStr, ','));
                if (CollUtil.containsAny(allowRoles, roles) || CollUtil.containsAny(allowUsers, users)) {
                    final String dbFieldName = dict.getStr("db_field_name");
                    if (StrUtil.contains(dbFieldName, "::")) {
                        final String[] strings = StrUtil.split(dbFieldName, "::");
                        final Dict convertDict = Convert.convert(Dict.class, retDict.getOrDefault(strings[0], Dict.create()));
                        convertDict.set(StrUtil.format("{}->>'$.{}'", strings[0], strings[1]), String.join("_", strings));
                        retDict.set(strings[0], convertDict);
                    } else {
                        retDict.set(dbFieldName, dbFieldName);
                    }
                }
            }
        }
        return retDict;
    }

    @Override
    @Cacheable(value = "__DEFAULT__", key = "targetClass + methodName + #coreModelId + #param")
    public Dict getModelAttributePermissionDeny(int coreModelId, Dict param) {
        final String ROLES_KEY = "roles";
        final String USERS_KEY = "users";
        final List<Dict> attributes = baseMapper.getModelAttributePermissionByModelId(Dict.create().set(GXCommonConstants.CORE_MODEL_PRIMARY_NAME, coreModelId));
        final List<Integer> roles = Convert.convert(new TypeReference<List<Integer>>() {
        }, param.getObj(ROLES_KEY));
        final List<Integer> users = Convert.convert(new TypeReference<List<Integer>>() {
        }, param.getObj(USERS_KEY));
        final Dict retDict = Dict.create();
        for (Dict dict : attributes) {
            if (null != dict.getStr("deny")) {
                final Dict denyDict = JSONUtil.toBean(dict.getStr("deny"), Dict.class);
                final String rolesStr = Optional.ofNullable(denyDict.getStr(ROLES_KEY)).orElse("");
                final String usersStr = Optional.ofNullable(denyDict.getStr(USERS_KEY)).orElse("");
                final List<Integer> denyRoles = Convert.convert(new TypeReference<List<Integer>>() {
                }, Strings.split(rolesStr, ','));
                final List<Integer> denyUsers = Convert.convert(new TypeReference<List<Integer>>() {
                }, Strings.split(usersStr, ','));
                if (CollUtil.containsAny(denyRoles, roles) || CollUtil.containsAny(denyUsers, users)) {
                    continue;
                }
                final String dbFieldName = dict.getStr("db_field_name");
                if (StrUtil.contains(dbFieldName, "::")) {
                    final String[] strings = StrUtil.split(dbFieldName, "::");
                    final Dict convertDict = Convert.convert(Dict.class, retDict.getOrDefault(strings[0], Dict.create()));
                    convertDict.set(StrUtil.format("{}->>'$.{}'", strings[0], strings[1]), String.join("_", strings));
                    retDict.set(strings[0], convertDict);
                } else {
                    retDict.set(dbFieldName, dbFieldName);
                }
            }
        }
        return retDict;
    }
}
