package com.geoxus.core.common.annotation;

import com.geoxus.core.common.validator.GXValidateDBUnique;
import com.geoxus.core.common.validator.impl.GXValidateDBUniqueValidator;
import org.springframework.messaging.handler.annotation.Payload;

import javax.validation.Constraint;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * @author britton chen
 * @email britton@126.com
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = GXValidateDBUniqueValidator.class)
@Documented
public @interface GXValidateDBUniqueAnnotation {
    String message() default "该数据已经存在";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends GXValidateDBUnique> service();

    String fieldName();
}