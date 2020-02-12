package com.geoxus.core.common.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.annotation.GXCheckRequestVerifyCodeAnnotation;
import com.geoxus.core.common.constant.GXCommonConstant;
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
import java.util.Optional;

@Aspect
@Component
public class GXCheckRequestVerifyCodeAspect {
    @Pointcut("@annotation(com.geoxus.core.common.annotation.GXCheckRequestVerifyCodeAnnotation)")
    public void checkVerifyCodePointCut() {
    }

    @Around("checkVerifyCodePointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        final GXCheckRequestVerifyCodeAnnotation gxCheckRequestVerifyCodeAnnotation = method.getAnnotation(GXCheckRequestVerifyCodeAnnotation.class);
        final boolean annotationValue = gxCheckRequestVerifyCodeAnnotation.value();
        Dict param = Convert.convert(Dict.class, point.getArgs()[0]);
        if (!annotationValue) {
            return point.proceed(point.getArgs());
        }
        final int verifyType = Optional.ofNullable(param.getInt("verify_type")).orElse(0);
        if (verifyType == 0) {
            throw new GXException(GXResultCode.NEED_CAPTCHA);
        }
        if (verifyType == GXCommonConstant.SMS_VERIFY && null != param.getStr("phone") &&
                !getSendSMSService().verification(param.getStr("phone"), param.getStr("verify_code"))) {
            throw new GXException(GXResultCode.NEED_CAPTCHA);
        }
        if (verifyType == GXCommonConstant.CAPTCHA_VERIFY &&
                !getCaptchaService().checkCaptcha(param.getStr("uuid"), param.getStr("verify_code"))) {
            throw new GXException(GXResultCode.NEED_CAPTCHA);
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
