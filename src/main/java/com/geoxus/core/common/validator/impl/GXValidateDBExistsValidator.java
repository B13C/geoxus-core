package com.geoxus.core.common.validator.impl;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.annotation.GXValidateDBExistsAnnotation;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.validator.GXValidateDBExists;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * 验证数据是否存在的验证器
 *
 * @author zj chen <britton@126.com>
 */
@Slf4j
public class GXValidateDBExistsValidator implements ConstraintValidator<GXValidateDBExistsAnnotation, Object> {
    private GXValidateDBExists service;

    private String fieldName;

    @Override
    public void initialize(GXValidateDBExistsAnnotation exists) {
        Class<? extends GXValidateDBExists> clazz = exists.service();
        fieldName = exists.fieldName();
        service = GXSpringContextUtils.getBean(clazz);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return service.validateExists(o, fieldName, constraintValidatorContext, Dict.create());
    }
}