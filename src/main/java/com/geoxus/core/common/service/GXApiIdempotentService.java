package com.geoxus.core.common.service;

import cn.hutool.core.lang.Dict;

public interface GXApiIdempotentService {
    /**
     * 创建Api的Token
     *
     * @param param 参数
     * @return String
     */
    String createApiIdempotentToken(Dict param);

    /**
     * 验证Api的Token
     *
     * @param token token字符串
     * @return boolean
     */
    boolean checkApiIdempotentToken(String token);
}
