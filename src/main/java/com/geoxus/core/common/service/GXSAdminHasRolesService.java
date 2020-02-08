package com.geoxus.core.common.service;

import com.geoxus.core.common.entity.GXSAdminHasRolesEntity;

import java.util.HashSet;
import java.util.Set;

public interface GXSAdminHasRolesService<T extends GXSAdminHasRolesEntity> extends GXBusinessService<T> {
    /**
     * 获取当前人的角色列表
     *
     * @param adminId 为NULL是获取当前登录人的
     * @return
     */
    default Set<String> getAdminRoles(long adminId) {
        return new HashSet<>();
    }
}
