package com.geoxus.core.common.annotation;

import java.lang.annotation.*;

/**
 * API幂等注解
 */
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GXApiIdempotentAnnotation {
    int expires() default 60;
}
