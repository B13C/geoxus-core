package com.geoxus.core.common.aspect;

import com.geoxus.core.common.oauth.GXTokenManager;
import com.geoxus.core.common.util.GXHttpContextUtils;
import com.geoxus.core.common.util.GXResultUtils;
import com.geoxus.core.common.vo.GXResultCode;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
public class GXUploadFileLegalAspect {
    @Pointcut("@annotation(com.geoxus.core.common.annotation.GXUploadFileLegalAnnotation)")
    public void uploadFileLegalPointCut() {
    }

    @Around("uploadFileLegalPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        HttpServletRequest request = GXHttpContextUtils.getHttpServletRequest();
        assert request != null;
        if (null == request.getHeader(GXTokenManager.ADMIN_TOKEN) || null == request.getHeader(GXTokenManager.USER_TOKEN)) {
            return GXResultUtils.error(GXResultCode.NEED_PERMISSION);
        }
        return point.proceed();
    }
}
