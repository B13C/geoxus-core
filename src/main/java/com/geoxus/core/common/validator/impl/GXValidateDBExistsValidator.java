package com.geoxus.core.common.validator.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.annotation.GXValidateDBExistsAnnotation;
import com.geoxus.core.common.exception.GXException;
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

    private Class<?>[] groups;

    private String tableName;

    @Override
    public void initialize(GXValidateDBExistsAnnotation annotation) {
        Class<? extends GXValidateDBExists> clazz = annotation.service();
        fieldName = annotation.fieldName();
        groups = annotation.groups();
        service = GXSpringContextUtils.getBean(clazz);
        tableName = annotation.tableName();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        if (null == service) {
            throw new GXException(StrUtil.format("字段<{}>的值<{}>需要指定相应的Service进行验证...", fieldName, o));
        }
        return service.validateExists(o, fieldName, constraintValidatorContext, Dict.create().set("table_name", tableName));
    }
}