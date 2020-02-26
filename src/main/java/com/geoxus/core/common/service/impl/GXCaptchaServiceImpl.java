package com.geoxus.core.common.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.LineCaptcha;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.IdUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.service.GXCaptchaService;
import com.geoxus.core.common.util.GXCacheKeysUtils;
import com.geoxus.core.common.util.GXGuavaUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class GXCaptchaServiceImpl implements GXCaptchaService {
    @GXFieldCommentAnnotation(zh = "Guava 缓存组件")
    final Cache<Object, Object> guavaCache = GXGuavaUtils.getGuavaCacheExpireAfterWrite(1000, 5L * 60, TimeUnit.SECONDS);

    @Autowired
    private GXCacheKeysUtils cacheKeysUtils;

    @Override
    public Map<String, Object> getCaptcha(Dict param) {
        return createCaptcha(param);
    }

    @Override
    public boolean checkCaptcha(String uuid, String code) {
        String cacheKey = cacheKeysUtils.getCaptchaConfigKey(uuid);
        if (code.equalsIgnoreCase((String) guavaCache.getIfPresent(cacheKey))) {
            guavaCache.invalidate(cacheKey);
            return true;
        }
        return false;
    }

    /**
     * 生成验证码图片
     *
     * @return Map
     */
    private Map<String, Object> createCaptcha(Dict param) {
        int width = 200;
        int height = 100;
        if (null != param.getInt("width")) {
            width = param.getInt("width");
        }
        if (null != param.getInt("height")) {
            height = param.getInt("height");
        }
        Map<String, Object> result = new HashMap<>();
        String uuid = IdUtil.randomUUID();
        LineCaptcha lineCaptcha = CaptchaUtil.createLineCaptcha(width, height);
        final String base64Img = lineCaptcha.getImageBase64();
        final String code = lineCaptcha.getCode();
        final String cacheKey = cacheKeysUtils.getCaptchaConfigKey(uuid);
        guavaCache.put(cacheKey, code);
        result.put("uuid", uuid);
        result.put("base64", "data:image/png;base64," + base64Img);
        return result;
    }
}
