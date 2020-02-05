package com.geoxus.core.common.annotation;

import com.geoxus.core.common.validator.GXValidateDBExists;
import com.geoxus.core.common.validator.impl.GXValidateDBExistsValidator;
import com.geoxus.core.framework.service.GXCoreModelService;
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
@Constraint(validatedBy = GXValidateDBExistsValidator.class)
@Documented
public @interface GXValidateDBExistsAnnotation {
    String message() default "#{fieldName}对应的数据不存在";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    Class<? extends GXValidateDBExists> service() default GXCoreModelService.class;

    String fieldName() default "model_id";
}