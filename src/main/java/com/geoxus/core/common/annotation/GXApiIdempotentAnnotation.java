package com.geoxus.core.common.annotation;

import java.lang.annotation.*;

/**
 * API幂等注解
 * 可以防止同一表单提交多次的情况
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GXApiIdempotentAnnotation {
    int expires() default 60;
}
