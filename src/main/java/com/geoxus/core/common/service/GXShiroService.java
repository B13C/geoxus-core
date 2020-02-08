package com.geoxus.core.common.service;

import cn.hutool.core.lang.Dict;

import java.util.Set;

public interface GXShiroService {
    /**
     * 获取用户权限列表
     *
     * @param userId 用户ID
     * @return Set
     */
    Set<String> getAdminPermissions(long userId);

    /**
     * 获取用户角色列表
     *
     * @param userId 用户ID
     * @return Set
     */
    Set<String> getAdminRoles(long userId);

    /**
     * 查询admin
     *
     * @param adminId 管理员ID
     * @return SAdminEntity
     */
    Dict getAdminById(Long adminId);

    /**
     * 判断是否时超级管理员
     *
     * @param adminEntity
     * @return
     */
    boolean isSuperAdmin(Dict adminEntity);
}
