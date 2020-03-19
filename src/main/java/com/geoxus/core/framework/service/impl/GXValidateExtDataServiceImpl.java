package com.geoxus.core.framework.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.validator.GXValidateExtDataService;
import com.geoxus.core.framework.entity.GXCoreAttributesEntity;
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

    private static final String FIELD_NOT_EXISTS = "{}模型中的{}属性不存在";

    private static final String FIELD_NOT_MATCH = "{}模型中的{}属性格式错误,需要满足({})的验证条件";

    private static final String FIELD_VALUE_NOT_EXISTS = "{}模型中的{}属性的值{}不存在";

    private static final String MODEL_SETTING_NOT_EXISTS = "{}模型不存在,请先配置模型";

    @Autowired
    private GXCoreModelService coreModelService;

    @Autowired
    private GXCoreAttributesService coreAttributesService;

    @Autowired
    private GXCoreAttributeEnumsService coreAttributeEnumsService;

    @Autowired
    private GXCoreModelAttributesService coreModelAttributeService;

    @Override
    public boolean validateExtData(Object o, String modelIdentification, String subFiled, boolean isFullMatchAttribute, ConstraintValidatorContext context) throws UnsupportedOperationException {
        final String jsonStr = JSONUtil.toJsonStr(o);
        if (!JSONUtil.isJson(jsonStr)) {
            return false;
        }
        final int coreModelId = coreModelService.getModelIdByModelIdentification(modelIdentification);
        if (coreModelId <= 0) {
            throw new GXException(StrUtil.format(MODEL_SETTING_NOT_EXISTS, modelIdentification));
        }
        if (isFullMatchAttribute && !StrUtil.equals(jsonStr, "{}")) {
            final boolean b = coreModelAttributeService.checkCoreModelFieldAttributes(coreModelId, subFiled, jsonStr);
            if (!b) {
                throw new GXException(StrUtil.format("{}字段提交的属性与数据库配置的字段属性不匹配!", subFiled));
            }
        }
        final GXCoreModelEntity coreModelEntity = coreModelService.getCoreModelByModelId(coreModelId, subFiled);
        final List<Dict> attributesList = coreModelEntity.getCoreAttributes();
        final Dict validateRule = Dict.create();
        for (Dict dict : attributesList) {
            validateRule.set(dict.getStr("attribute_name"), dict.getStr("validation_expression"));
        }
        if (JSONUtil.isJsonObj(jsonStr)) {
            final Dict validateDataMap = Convert.convert(Dict.class, JSONUtil.toBean(jsonStr, Dict.class));
            return !dataValidation(modelIdentification, coreModelId, validateRule, validateDataMap, context, -1);
        } else {
            final JSONArray jsonArray = JSONUtil.parseArray(jsonStr);
            int currentIndex = 0;
            for (Object object : jsonArray) {
                final Dict validateDataMap = Convert.convert(Dict.class, object);
                if (dataValidation(modelIdentification, coreModelId, validateRule, validateDataMap, context, currentIndex++)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 数据验证
     *
     * @param modelIdentification 模型标识
     * @param modelId             模型ID
     * @param validateRule        验证规则
     * @param validateDataMap     需要验证的数据
     * @param context             验证组件上下文对象
     * @return boolean
     */
    private boolean dataValidation(String modelIdentification, int modelId, Dict validateRule, Map<String, Object> validateDataMap, ConstraintValidatorContext context, int currentIndex) {
        final Set<String> keySet = validateDataMap.keySet();
        for (String field : keySet) {
            final boolean b = coreModelService.checkModelHasAttribute(modelId, field);
            final String errorInfo = currentIndex > -1 ? currentIndex + "." + field : field;
            if (!b) {
                context.buildConstraintViolationWithTemplate(StrUtil.format(FIELD_NOT_EXISTS, modelIdentification, field)).addPropertyNode(errorInfo).addConstraintViolation();
                return true;
            }
            final GXCoreAttributesEntity attribute = coreAttributesService.getAttributeByAttributeName(field);
            Dict modelAttributesData = coreModelAttributeService.getModelAttributeByModelIdAndAttributeId(modelId, attribute.getAttributeId());
            String rule = modelAttributesData.getStr("validation_expression");
            if (StrUtil.isBlank(rule)) {
                rule = Convert.toStr(validateRule.get(field));
            }
            if (StrUtil.isBlank(rule) && modelAttributesData.getInt("force_validation") == 0) {
                // 不验证当前数据
                return false;
            }
            final String value = Convert.toStr(validateDataMap.get(field));
            if (StrUtil.isBlank(rule)) {
                return true;
            }
            final boolean isMatch = Pattern.matches(rule, value);
            if (!isMatch && modelAttributesData.getInt("required") == VERIFY_VALUE) {
                context.buildConstraintViolationWithTemplate(StrUtil.format(FIELD_NOT_MATCH, modelIdentification, field, rule)).addPropertyNode(errorInfo).addConstraintViolation();
                return true;
            }
            if (!coreAttributeEnumsService.isExistsAttributeValue(attribute.getAttributeId(), value, modelId)) {
                context.buildConstraintViolationWithTemplate(StrUtil.format(FIELD_VALUE_NOT_EXISTS, modelIdentification, field, value)).addPropertyNode(field).addConstraintViolation();
                return true;
            }
        }
        return false;
    }
}
