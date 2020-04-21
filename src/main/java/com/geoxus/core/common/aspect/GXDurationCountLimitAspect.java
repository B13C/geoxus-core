package com.geoxus.core.common.aspect;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.annotation.GXFrequencyLimitAnnotation;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.util.GXHttpContextUtils;
import com.geoxus.core.common.util.GXRedisUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Aspect
@Component
public class GXDurationCountLimitAspect {
    @GXFieldCommentAnnotation(zh = "缓存前缀")
    private static final String CACHE_KEY_PFEFIX = "duration:count:limit:";

    @Pointcut("@annotation(com.geoxus.core.common.annotation.GXFrequencyLimitAnnotation)")
    public void frequencyLimitPointCut() {
    }

    @Around("frequencyLimitPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method method = signature.getMethod();
        final GXFrequencyLimitAnnotation durationCountLimitAnnotation = method.getAnnotation(GXFrequencyLimitAnnotation.class);
        final int count = durationCountLimitAnnotation.count();
        String s = durationCountLimitAnnotation.key();
        if (StrUtil.isBlank(s)) {
            s = RandomUtil.randomString(8);
        }
        String key = CACHE_KEY_PFEFIX.concat(s);
        final int expire = durationCountLimitAnnotation.expire();
        final String scene = durationCountLimitAnnotation.scene();
        String sceneValue = scene;
        if ("ip".equals(scene)) {
            sceneValue = GXHttpContextUtils.getClientIP();
        }
        key = key.concat(sceneValue);
        final long actualCount = GXRedisUtils.getCounter(key, expire, TimeUnit.SECONDS);
        if (actualCount > count) {
            throw new GXException("操作频繁,请稍后在试......");
        }
        return point.proceed(point.getArgs());
    }
}
