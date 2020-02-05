package com.geoxus.core.common.service;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.entity.GXUUserEntity;
import com.geoxus.core.common.validator.GXValidateDBExists;

public interface GXUUserService<T extends GXUUserEntity> extends GXBusinessService<T>, GXValidateDBExists {
    /**
     * 验证前端用户的Token是否有效
     *
     * @param token
     * @return
     */
    default Dict verifyUserToken(String token) {
        return Dict.create();
    }
}
