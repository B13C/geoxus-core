package com.geoxus.core.common.service;

import com.geoxus.core.common.entity.GXSPermissionsEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public interface GXSPermissionsService<T extends GXSPermissionsEntity> extends GXBusinessService<T> {
    /**
     * 获取当前人的权限Code
     *
     * @param adminId 为NULL是获取当前登录人的
     * @return
     */
    default Set<String> getAdminPermissions(long adminId) {
        return new HashSet<>();
    }

    /**
     * 获取角色的权限ID集合
     *
     * @param roleId
     * @return
     */
    default List<Integer> getRolePermissions(Integer roleId) {
        return Collections.emptyList();
    }
}
