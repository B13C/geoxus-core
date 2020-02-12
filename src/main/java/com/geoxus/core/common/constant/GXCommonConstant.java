package com.geoxus.core.common.constant;

import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;

public class GXCommonConstant {
    @GXFieldCommentAnnotation(zh = "手机验证码标识")
    public static final int SMS_VERIFY = 1;

    @GXFieldCommentAnnotation(zh = "图形验证码标识")
    public static final int CAPTCHA_VERIFY = 2;

    @GXFieldCommentAnnotation(zh = "电子邮箱验证码标识")
    public static final int EMAIL_VERIFY = 3;
}
