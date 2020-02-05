package com.geoxus.core.common.validator.impl;

import com.geoxus.core.common.annotation.GXValidateExtDataAnnotation;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.validator.GXValidateExtDataService;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class GXValidateExtDataValidator implements ConstraintValidator<GXValidateExtDataAnnotation, Object> {
    private GXValidateExtDataService service;

    private String model;

    private String subField;

    @Override
    public void initialize(GXValidateExtDataAnnotation constraintAnnotation) {
        Class<? extends GXValidateExtDataService> clazz = constraintAnnotation.service();
        model = constraintAnnotation.tableName();
        subField = constraintAnnotation.fieldName();
        service = GXSpringContextUtils.getBean(clazz);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext context) {
        if (null == o) {
            return true;
        }
        return service.validateExtData(o, model, subField, context);
    }
}
