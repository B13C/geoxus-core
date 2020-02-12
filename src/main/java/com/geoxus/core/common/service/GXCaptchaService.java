package com.geoxus.core.common.service;

import java.util.Map;

/**
 * 生成验证码接口
 */
public interface GXCaptchaService {
    /**
     * 生成验证码
     *
     * @return
     */
    Map<String, Object> getCaptcha();

    /**
     * 验证验证码
     *
     * @param uuid
     * @param code
     * @return
     */
    boolean checkCaptcha(String uuid, String code);
}
