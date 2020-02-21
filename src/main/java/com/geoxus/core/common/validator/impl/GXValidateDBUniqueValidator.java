package com.geoxus.core.common.validator.impl;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.annotation.GXValidateDBUniqueAnnotation;
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
public class GXValidateDBUniqueValidator implements ConstraintValidator<GXValidateDBUniqueAnnotation, Object> {
    private GXValidateDBUnique service;

    private String field;

    @Override
    public void initialize(GXValidateDBUniqueAnnotation unique) {
        Class<? extends GXValidateDBUnique> clazz = unique.service();
        field = unique.fieldName();
        service = GXSpringContextUtils.getBean(clazz);
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        return !service.validateUnique(o, field, constraintValidatorContext, Dict.create());
    }
}