package com.geoxus.core.common.service;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.entity.GXSAdminHasRolesEntity;

import java.util.Collections;

public interface GXSAdminHasRolesService<T extends GXSAdminHasRolesEntity> extends GXBusinessService<T> {
    /**
     * 获取当前人的角色列表
     *
     * @param adminId 为NULL是获取当前登录人的
     * @return
     */
    default Dict getAdminRoles(long adminId) {
        return Dict.create();
    }
}
