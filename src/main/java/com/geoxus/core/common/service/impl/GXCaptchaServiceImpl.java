package com.geoxus.core.common.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.util.IdUtil;
import com.geoxus.core.common.util.GXRedisKeysUtils;
import com.geoxus.core.common.util.GXRedisUtils;
import com.geoxus.core.common.service.GXCaptchaService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class GXCaptchaServiceImpl implements GXCaptchaService {
    @Autowired
    private GXRedisUtils redisUtils;

    @Autowired
    private GXRedisKeysUtils redisKeysUtils;

    @Override
    public Map<String, Object> getCaptcha() {
        return createCaptcha();
    }

    @Override
    public boolean checkCaptcha(String uuid, String code) {
        String key = redisKeysUtils.getCaptchaConfigKey(uuid);
        if (code.equalsIgnoreCase(redisUtils.get(key))) {
            redisUtils.delete(key);
            return true;
        }
        return false;
    }

    /**
     * 生成验证码图片
     *
     * @return Map
     */
    private Map<String, Object> createCaptcha() {
        Map<String, Object> result = new HashMap<>();
        String uuid = IdUtil.randomUUID();
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(200, 100);
        final String base64Img = lineCaptcha.getImageBase64();
        final String code = lineCaptcha.getCode();
        redisUtils.set(redisKeysUtils.getCaptchaConfigKey(uuid), code, 5L * 60);
        result.put("uuid", uuid);
        result.put("base64", "data:image/png;base64," + base64Img);
        return result;
    }
}
