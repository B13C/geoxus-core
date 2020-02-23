package com.geoxus.core.framework.service;

import com.geoxus.core.framework.entity.GXCoreConfigEntity;

public interface GXCoreConfigService extends GXBaseService<GXCoreConfigEntity> {
    String PRIMARY_KEY = "config_id";

    <T> T getConfigObject(String key, Class<T> clazz);

    boolean updateValueByParamKey(String cloudStorageConfigKey, String toJsonStr);
}
