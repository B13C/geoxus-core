package com.geoxus.core.common.service.impl;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.entity.GXSAdminEntity;
import com.geoxus.core.common.service.GXSAdminHasRolesService;
import com.geoxus.core.common.service.GXSAdminService;
import com.geoxus.core.common.service.GXSPermissionsService;
import com.geoxus.core.common.service.GXShiroService;
import com.geoxus.core.common.util.GXCommonUtils;
import com.geoxus.core.common.util.GXSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Slf4j
public class GXShiroServiceImpl implements GXShiroService {
    /**
     * 获取用户权限列表
     *
     * @param adminId
     */
    public Set<String> getAdminAllPermissions(Long adminId) {
        return GXSpringContextUtils.getBean(GXSPermissionsService.class).getAdminAllPermissions(adminId);
    }

    /**
     * 获取用户角色列表
     */
    public Set<String> getAdminRoles(long adminId) {
        Set<String> set = GXSpringContextUtils.getBean(GXSAdminHasRolesService.class).getAdminRoles(adminId);
        return set;
    }

    @Override
    public Dict getAdminById(Long adminId) {
        GXSAdminEntity adminEntity = (GXSAdminEntity) GXSpringContextUtils.getBean(GXSAdminService.class).getById(adminId);
        if (null != adminEntity) {
            return Dict.parse(adminEntity);
        }
        return Dict.create();
    }

    @Override
    public boolean isSuperAdmin(Dict adminData) {
        return adminData.getLong("isSuperAdmin").equals(GXCommonUtils.getEnvironmentValue("super.admin.id", Long.class));
    }
}
