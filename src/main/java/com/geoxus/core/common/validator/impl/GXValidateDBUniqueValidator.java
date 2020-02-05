package com.geoxus.core.common.validator.impl;

import com.geoxus.core.common.annotation.GXDBUniqueAnnotation;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.validator.GXValidateDBUnique;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 验证数据是否唯一验证器
 *
 * @author zj chen <britton@126.com>
 */
@Slf4j
public class GXValidateDBUniqueValidator implements ConstraintValidator<GXDBUniqueAnnotation, Object> {
    private GXValidateDBUnique service;

    private String field;

    @Override
    public void initialize(GXDBUniqueAnnotation unique) {
        Class<? extends GXValidateDBUnique> clazz = unique.service();
        field = unique.field();
        service = GXSpringContextUtils.getBean(clazz);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return service.validateUnique(o, field);
    }
}