package com.geoxus.core.framework.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.validator.GXValidateExtDataService;
import com.geoxus.core.framework.entity.GXCoreAttributesEntity;
import com.geoxus.core.framework.entity.GXCoreModelAttributesEntity;
import com.geoxus.core.framework.entity.GXCoreModelEntity;
import com.geoxus.core.framework.service.GXCoreAttributeEnumsService;
import com.geoxus.core.framework.service.GXCoreAttributesService;
import com.geoxus.core.framework.service.GXCoreModelAttributesService;
import com.geoxus.core.framework.service.GXCoreModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Service
public class GXValidateExtDataServiceImpl implements GXValidateExtDataService {
    private static final int VERIFY_VALUE = 1;

    private static final String FIELD_NOT_EXISTS = "{}模型中不存在{}属性";

    private static final String FIELD_NOT_MATCH = "{}模型中{}属性格式错误({})......";

    private static final String FIELD_VALUE_NOT_EXISTS = "{}模型中{}属性枚举值{}不存在......";

    private static final String MODEL_SETTING_NOT_EXISTS = "{}不存在,请先配置模型......";

    @Autowired
    private GXCoreModelService coreModelService;

    @Autowired
    private GXCoreAttributesService coreAttributesService;

    @Autowired
    private GXCoreAttributeEnumsService coreAttributeEnumsService;

    @Autowired
    private GXCoreModelAttributesService coreModelAttributeService;

    @Override
    public boolean validateExtData(Object o, String model, String subFiled, ConstraintValidatorContext context) throws UnsupportedOperationException {
        final String jsonStr = JSONUtil.toJsonStr(o);
        if (!JSONUtil.isJson(jsonStr)) {
            return false;
        }
        final int modelId = coreModelService.getModelIdByModelIdentification(model);
        if (modelId <= 0) {
            throw new GXException(StrUtil.format(MODEL_SETTING_NOT_EXISTS, model));
        }
        final GXCoreModelEntity modelEntity = coreModelService.getModelDetailByModelId(modelId, subFiled);
        final List<GXCoreModelAttributesEntity> attributesList = modelEntity.getCoreAttributesEntities();
        final Dict validateRule = Dict.create();
        for (GXCoreModelAttributesEntity defaultAttributeEntity : attributesList) {
            validateRule.set(defaultAttributeEntity.getFieldName(), defaultAttributeEntity.getValidationExpression());
        }
        if (JSONUtil.isJsonObj(jsonStr)) {
            final Dict validateDataMap = Convert.convert(Dict.class, JSONUtil.toBean(jsonStr, Dict.class));
            return !dataValidation(model, modelId, validateRule, validateDataMap, context, -1);
        } else {
            final JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
            int currentIndex = 0;
            for (Object object : jsonArray) {
                final Dict validateDataMap = Convert.convert(Dict.class, object);
                if (dataValidation(model, modelId, validateRule, validateDataMap, context, currentIndex++)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 数据验证
     *
     * @param model
     * @param modelId
     * @param validateRule
     * @param validateDataMap
     * @param context
     * @return
     */
    private boolean dataValidation(String model, int modelId, Dict validateRule, Map<String, Object> validateDataMap, ConstraintValidatorContext context, int currentIndex) {
        final Set<String> keySet = validateDataMap.keySet();
        for (String field : keySet) {
            final boolean b = coreModelService.checkModelIsHasField(modelId, field);
            final String errorInfo = currentIndex > -1 ? currentIndex + "." + field : field;
            if (!b) {
                context.buildConstraintViolationWithTemplate(StrUtil.format(FIELD_NOT_EXISTS, model, field)).addPropertyNode(errorInfo).addConstraintViolation();
                return true;
            }
            final GXCoreAttributesEntity attribute = coreAttributesService.getAttributeByFieldName(field);
            GXCoreModelAttributesEntity modelAttributeGroupEntity = coreModelAttributeService.getModelAttributeByModelIdAndAttributeId(modelId, attribute.getAttributeId());
            String rule = modelAttributeGroupEntity.getValidationExpression();
            if (StrUtil.isBlank(rule)) {
                rule = Convert.toStr(validateRule.get(field));
            }
            if (StrUtil.isBlank(rule) && modelAttributeGroupEntity.getForceValidation() == 0) {
                // 不验证当前数据
                return false;
            }
            final String value = Convert.toStr(validateDataMap.get(field));
            if (StrUtil.isBlank(rule)) {
                return true;
            }
            final boolean isMatch = Pattern.matches(rule, value);
            if (modelAttributeGroupEntity.getRequired() == VERIFY_VALUE && !isMatch) {
                context.buildConstraintViolationWithTemplate(StrUtil.format(FIELD_NOT_MATCH, model, field, rule)).addPropertyNode(errorInfo).addConstraintViolation();
                return true;
            }
            if (coreAttributeEnumsService.isExistsAttribute(attribute.getAttributeId(), modelId)
                    && !coreAttributeEnumsService.isExistsAttributeValue(attribute.getAttributeId(), value, modelId)) {
                context.buildConstraintViolationWithTemplate(StrUtil.format(FIELD_VALUE_NOT_EXISTS, model, field, value)).addPropertyNode(field).addConstraintViolation();
                return true;
            }
        }
        return false;
    }
}
