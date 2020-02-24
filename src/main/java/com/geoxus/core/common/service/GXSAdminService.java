package com.geoxus.core.common.service;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.entity.GXSAdminEntity;

public interface GXSAdminService<T extends GXSAdminEntity> extends GXBusinessService<T> {
    /**
     * 获取当前登录管理员的状态
     *
     * @param adminId 管理员ID
     * @return
     */
    Dict getStatus(long adminId);
}
