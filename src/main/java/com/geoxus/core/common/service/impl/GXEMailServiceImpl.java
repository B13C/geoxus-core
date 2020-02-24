package com.geoxus.core.common.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.mail.MailUtil;
import com.geoxus.core.common.service.GXEMailService;
import com.geoxus.core.common.util.GXCacheKeysUtils;
import com.geoxus.core.common.util.GXRedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GXEMailServiceImpl implements GXEMailService {
    @Autowired
    private GXRedisUtils gxRedisUtils;

    @Autowired
    private GXCacheKeysUtils gxCacheKeysUtils;

    @Override
    public boolean sendVerifyCode(String email) {
        final String code = RandomUtil.randomString(6);
        final String format = StrUtil.format("本次修改密码验证码是 : {} , 有效期为5分钟", code);
        final String redisKey = gxCacheKeysUtils.getCacheKey("sys.email.verify.code", email);
        final String sendResult = MailUtil.send(email, "修改密码验证码", format, false);
        if (StrUtil.isNotBlank(sendResult)) {
            gxRedisUtils.set(redisKey, code, 300);
            return true;
        }
        return false;
    }

    @Override
    public boolean verification(String email, String code) {
        final String redisKey = gxCacheKeysUtils.getCacheKey("sys.email.verify.code", email);
        final String value = gxRedisUtils.get(redisKey);
        if (null != value) {
            if (!StrUtil.equalsAnyIgnoreCase(code, value)) {
                return false;
            }
            return gxRedisUtils.delete(redisKey);
        }
        return false;
    }
}
