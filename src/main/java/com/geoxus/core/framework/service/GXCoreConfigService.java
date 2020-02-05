package com.geoxus.core.framework.service;

import com.geoxus.core.common.service.GXBusinessService;
import com.geoxus.core.framework.entity.CoreConfigEntity;

public interface GXCoreConfigService extends GXBusinessService<CoreConfigEntity> {
    String PRIMARY_KEY = "id";
}
