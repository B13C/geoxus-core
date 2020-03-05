package com.geoxus.core.common.service.impl;

import cn.hutool.core.lang.Dict;
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
     * @return
     */
    public Dict getAdminRoles(long adminId) {
        return GXSpringContextUtils.getBean(GXSAdminHasRolesService.class).getAdminRoles(adminId);
    }

    @Override
    public Dict getAdminById(Long adminId) {
        final Dict dict = GXSpringContextUtils.getBean(GXSAdminService.class).getStatus(adminId);
        if (null == dict) {
            return Dict.create();
        }
        return dict;
    }

    @Override
    public boolean isSuperAdmin(Dict adminData) {
        final String primaryKey = GXSpringContextUtils.getBean(GXSAdminService.class).getPrimaryKey();
        if (null != adminData.getLong(primaryKey)) {
            return adminData.getLong(primaryKey).equals(GXCommonUtils.getEnvironmentValue("super.admin.id", Long.class));
        }
        return adminData.getLong("is_super_admin") == 1;
    }
}
