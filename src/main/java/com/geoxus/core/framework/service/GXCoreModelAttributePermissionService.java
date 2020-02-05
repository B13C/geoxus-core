package com.geoxus.core.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.geoxus.core.framework.entity.CoreModelAttributePermissionEntity;

import java.util.List;

public interface GXCoreModelAttributePermissionService extends IService<CoreModelAttributePermissionEntity> {
    /**
     * 通过核心模型Id获取模型属性的权限
     *
     * @param coreModelId
     * @return
     */
    List<String> getModelAttributePermissionByCoreModelId(int coreModelId);
}
