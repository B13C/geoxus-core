package com.geoxus.core.common.service;

import cn.hutool.core.lang.Dict;

public interface GXApiIdempotentService {
    String API_IDEMPOTENT_TOKEN = "api-token";

    String createApiIdempotentToken(Dict param);

    boolean checkApiIdempotentToken(String token);
}
