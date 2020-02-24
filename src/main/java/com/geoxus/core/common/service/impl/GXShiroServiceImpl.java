package com.geoxus.core.common.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.entity.GXSAdminEntity;
import com.geoxus.core.common.service.GXSAdminHasRolesService;
import com.geoxus.core.common.service.GXSAdminService;
import com.geoxus.core.common.service.GXSPermissionsService;
import com.geoxus.core.common.service.GXShiroService;
import com.geoxus.core.common.util.GXCommonUtils;
import com.geoxus.core.common.util.GXSpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
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
        final String primaryKey = StrUtil.toCamelCase(GXSpringContextUtils.getBean(GXSAdminService.class).getPrimaryKey());
        if (null != adminData.getLong(primaryKey)) {
            return adminData.getLong(primaryKey).equals(GXCommonUtils.getEnvironmentValue("super.admin.id", Long.class));
        }
        return adminData.getLong("isSuperAdmin") == 1;
    }
}
