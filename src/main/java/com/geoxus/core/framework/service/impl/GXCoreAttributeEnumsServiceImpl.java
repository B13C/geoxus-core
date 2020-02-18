package com.geoxus.core.framework.service.impl;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.constant.GXCommonConstant;
import com.geoxus.core.framework.entity.GXCoreAttributesEntity;
import com.geoxus.core.framework.entity.GXCoreAttributesEnumsEntity;
import com.geoxus.core.framework.mapper.GXCoreAttributeEnumsMapper;
import com.geoxus.core.framework.service.GXCoreAttributeEnumsService;
import com.geoxus.core.framework.service.GXCoreAttributesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidatorContext;
import java.util.List;

@Service
public class GXCoreAttributeEnumsServiceImpl extends ServiceImpl<GXCoreAttributeEnumsMapper, GXCoreAttributesEnumsEntity> implements GXCoreAttributeEnumsService {
    @GXFieldCommentAnnotation(zh = "字段不存在的提示")
    private static final String FIELD_VALUE_NOT_EXISTS = "{}属性不存在值{}";

    @GXFieldCommentAnnotation(zh = "标识核心模型主键名字")
    private static final String CORE_MODEL_PRIMARY_NAME = GXCommonConstant.CORE_MODEL_PRIMARY_NAME;

    @Autowired
    private GXCoreAttributesService coreAttributesService;

    @Override
    public boolean isExistsAttributeValue(int attributeId, Object value, int coreModelId) {
        final Dict condition = Dict.create();
        condition.set("attribute_id", attributeId);
        condition.set(CORE_MODEL_PRIMARY_NAME, coreModelId);
        condition.set("value_enum", value);
        GXCoreAttributesEnumsEntity entity = getOne(new QueryWrapper<GXCoreAttributesEnumsEntity>().allEq(condition));
        return entity != null;
    }

    @Override
    public boolean isExistsAttribute(int attributeId, int coreModelId) {
        final Dict condition = Dict.create().set("attribute_id", attributeId).set("core_model_id", coreModelId);
        final int cnt = baseMapper.exists(condition);
        return cnt != 0;
    }

    @Override
    @Cacheable(value = "attribute_enums", key = "targetClass + methodName + #p0.getStr('attribute_name')")
    public List<Dict> getAttributeEnumsByCondition(Dict condition) {
        final Page<Dict> page = new Page<>(1, 500);
        return baseMapper.listOrSearchPage(page, condition);
    }

    @Override
    public boolean validateExists(Object value, String field, ConstraintValidatorContext constraintValidatorContext, Dict param) throws UnsupportedOperationException {
        Dict dict = JSONUtil.toBean(JSONUtil.toJsonStr(value), Dict.class);
        String attributeValue = dict.getStr(field);
        if (null != attributeValue) {
            GXCoreAttributesEntity attributesEntity = coreAttributesService.getAttributeByFieldName(field);
            if (null != attributesEntity) {
                final int coreModelId = param.getInt("core_model_id");
                boolean exists = isExistsAttributeValue(attributesEntity.getAttributeId(), attributeValue, coreModelId);
                if (!exists) {
                    constraintValidatorContext.buildConstraintViolationWithTemplate(StrUtil.format(FIELD_VALUE_NOT_EXISTS, field, attributeValue)).addPropertyNode(field).addConstraintViolation();
                }
                return exists;
            }
        }
        return true;
    }
}
