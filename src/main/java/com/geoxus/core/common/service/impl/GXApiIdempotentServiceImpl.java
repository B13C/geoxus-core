package com.geoxus.core.common.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.service.GXApiIdempotentService;
import com.geoxus.core.common.util.GXRedisKeysUtils;
import com.geoxus.core.common.util.GXRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GXApiIdempotentServiceImpl implements GXApiIdempotentService {
    private static final String API_PREFIX_KEY = "idempotent";
    private static final int EXPIRE_TIME_SECOND = 300;
    @Autowired
    private GXRedisUtils redisUtils;
    @Autowired
    private GXRedisKeysUtils redisKeysUtils;

    @Override
    public String createApiIdempotentToken(Dict param) {
        final String s = getTokenValue(SecureUtil.md5(Optional.ofNullable(param.getStr("salt")).orElse("geoxus-default") + RandomUtil.randomString(10)));
        final String idempotentKey = redisKeysUtils.getRedisKey("request.api.idempotent", API_PREFIX_KEY);
        redisUtils.set(StrUtil.format("{}-{}", idempotentKey, s), 1, EXPIRE_TIME_SECOND);
        return s;
    }

    @Override
    public boolean checkApiIdempotentToken(String token) {
        token = StrUtil.format("{}-{}", redisKeysUtils.getRedisKey("request.api.idempotent", API_PREFIX_KEY), token);
        final String s = Optional.ofNullable(redisUtils.get(token)).orElse("");
        if (s.isEmpty()) {
            throw new GXException("API TOKEN不能为空");
        }
        final boolean b = redisUtils.delete(token);
        if (!b) {
            throw new GXException("API TOKEN无效");
        }
        return true;
    }

    private String getTokenValue(String initStr) {
        if (initStr.isEmpty()) {
            return IdUtil.randomUUID();
        }
        final String idempotentKey = redisKeysUtils.getRedisKey("request.api.idempotent", API_PREFIX_KEY);
        return StrUtil.format("{}-{}", idempotentKey, initStr);
    }
}
