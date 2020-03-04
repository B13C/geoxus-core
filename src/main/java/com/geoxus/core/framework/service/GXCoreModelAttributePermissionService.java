package com.geoxus.core.framework.service;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.framework.entity.GXCoreModelAttributesPermissionEntity;

import java.util.List;

public interface GXCoreModelAttributePermissionService extends GXBaseService<GXCoreModelAttributesPermissionEntity> {
    /**
     * 通过核心模型Id获取模型属性的权限
     *
     * @param coreModelId 核心模型ID
     * @return
     */
    List<String> getModelAttributePermissionByCoreModelId(int coreModelId);

    /**
     * 获取扩展字段的允许访问列表
     *
     * @param coreModelId 核心模型ID
     * @param param       附加参数
     * @return
     */
    List<Dict> getModelAttributePermissionAllow(int coreModelId, Dict param);

    /**
     * 获取扩展字段的拒绝访问列表
     *
     * @param coreModelId 核心模型ID
     * @param param       附加参数
     * @return
     */
    List<Dict> getModelAttributePermissionDeny(int coreModelId, Dict param);
}
