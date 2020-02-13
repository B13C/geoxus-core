package com.geoxus.core.common.aspect;

import cn.hutool.core.convert.Convert;
import com.geoxus.core.common.annotation.GXDurationCountLimitAnnotation;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.util.GXHttpContextUtils;
import com.geoxus.core.common.util.GXRedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Component
public class GXDurationCountLimitAspect {
    @GXFieldCommentAnnotation(zh = "缓存前缀")
    private static final String CACHE_KEY_PFEFIX = "duration:count:limit:";

    @Autowired
    private GXRedisUtils gxRedisUtils;

    @Pointcut("@annotation(com.geoxus.core.common.annotation.GXDurationCountLimitAnnotation)")
    public void durationCountLimitPointCut() {
    }

    @Around("durationCountLimitPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        int actualCount;
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        final GXDurationCountLimitAnnotation durationCountLimitAnnotation = method.getAnnotation(GXDurationCountLimitAnnotation.class);
        final int count = durationCountLimitAnnotation.count();
        String key = CACHE_KEY_PFEFIX.concat(durationCountLimitAnnotation.key());
        final int expire = durationCountLimitAnnotation.expire();
        final String scene = durationCountLimitAnnotation.scene();
        if (scene.equals("ip")) {
            key = key.concat(GXHttpContextUtils.getIP());
        }
        final String s = gxRedisUtils.get(key);
        if (null != s) {
            actualCount = Convert.convert(Integer.class, s);
            if (actualCount >= count) {
                throw new GXException("操作频繁,请稍后在试......");
            }
            gxRedisUtils.increment(key);
        } else {
            gxRedisUtils.set(key, 1, expire);
        }
        return point.proceed(point.getArgs());
    }
}
