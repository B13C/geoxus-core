package com.geoxus.core.common.annotation;

import java.lang.annotation.*;

/**
 * 用于反射读取接口/类中定义的属性信息
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GXFieldCommentAnnotation {
    String value() default "";

    String zh() default "中文描述";

    String en() default "英文描述";

    long code() default 0;

    boolean show() default false;
}
