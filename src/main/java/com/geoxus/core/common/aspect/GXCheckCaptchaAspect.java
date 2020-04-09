package com.geoxus.core.common.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.annotation.GXCheckCaptchaAnnotation;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.service.GXCaptchaService;
import com.geoxus.core.common.service.GXSendSMSService;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.vo.GXResultCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class GXCheckCaptchaAspect {
    @Pointcut("@annotation(com.geoxus.core.common.annotation.GXCheckCaptchaAnnotation)")
    public void checkCaptchaPointCut() {
    }

    @Around("checkCaptchaPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        final GXCheckCaptchaAnnotation gxCheckCaptchaAnnotation = method.getAnnotation(GXCheckCaptchaAnnotation.class);
        final boolean annotationValue = gxCheckCaptchaAnnotation.value();
        final int verifyType = gxCheckCaptchaAnnotation.verifyType();
        Dict param = Convert.convert(Dict.class, point.getArgs()[0]);
        if (!annotationValue) {
            return point.proceed(point.getArgs());
        }
        if (verifyType == 0) {
            throw new GXException(GXResultCode.NEED_CAPTCHA);
        }
        final String verifyCode = param.getStr("verify_code");
        if (null == verifyCode) {
            String msg = "请传递手机验证码";
            if (verifyType == GXCommonConstants.CAPTCHA_VERIFY) {
                msg = "请传递图形验证码";
            }
            throw new GXException(msg);
        }
        if (verifyType == GXCommonConstants.SMS_VERIFY) {
            final String phone = param.getStr("phone");
            if (null == phone) {
                throw new GXException("请传递手机号码");
            }
            if (!getSendSMSService().verification(phone, verifyCode)) {
                throw new GXException(GXResultCode.NEED_CAPTCHA);
            }
        } else if (verifyType == GXCommonConstants.CAPTCHA_VERIFY) {
            final String uuid = param.getStr("uuid");
            if (null == uuid) {
                throw new GXException("请传递图形验证码标识uuid");
            }
            if (!getCaptchaService().checkCaptcha(uuid, verifyCode)) {
                throw new GXException(GXResultCode.NEED_CAPTCHA);
            }
        }
        return point.proceed(point.getArgs());
    }

    private GXSendSMSService getSendSMSService() {
        return GXSpringContextUtils.getBean(GXSendSMSService.class);
    }

    private GXCaptchaService getCaptchaService() {
        return GXSpringContextUtils.getBean(GXCaptchaService.class);
    }
}
