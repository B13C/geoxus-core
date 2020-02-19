package com.geoxus.core.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface GXRequestBodyToBeanAnnotation {
    String value() default "";

    Class<?>[] groups() default {};

    String primaryKey() default "id";
}
