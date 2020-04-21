package com.geoxus.core.common.interceptor;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.annotation.GXRequestBodyToEntityAnnotation;
import com.geoxus.core.common.constant.GXCommonConstants;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.util.GXCommonUtils;
import com.geoxus.core.common.validator.impl.GXValidatorUtils;
import com.geoxus.core.common.vo.GXResultCode;
import com.geoxus.core.framework.service.GXCoreModelAttributesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import static cn.hutool.core.map.MapUtil.filter;

@Component
public class GXRequestToBeanHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @GXFieldCommentAnnotation(zh = "请求中的参数名字")
    public static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    @Autowired
    private GXCoreModelAttributesService gxCoreModelAttributesService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(GXRequestBodyToEntityAnnotation.class);
    }

    @Override
    public Object resolveArgument(@NonNull MethodParameter parameter, ModelAndViewContainer mavContainer, @NonNull NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final String body = getRequestBody(webRequest);
        final Dict dict = Convert.convert(Dict.class, JSONUtil.toBean(body, Dict.class));
        if (null == dict.getInt(GXCommonConstants.CORE_MODEL_PRIMARY_NAME)) {
            throw new GXException(StrUtil.format("{}参数必传...", GXCommonConstants.CORE_MODEL_PRIMARY_NAME));
        }
        final Class<?> parameterType = parameter.getParameterType();
        final GXRequestBodyToEntityAnnotation gxRequestBodyToEntityAnnotation = parameter.getParameterAnnotation(GXRequestBodyToEntityAnnotation.class);
        final String value = Objects.requireNonNull(gxRequestBodyToEntityAnnotation).value();
        final String[] jsonFields = gxRequestBodyToEntityAnnotation.jsonFields();
        boolean fillJSONField = gxRequestBodyToEntityAnnotation.fillJSONField();
        boolean validateEntity = gxRequestBodyToEntityAnnotation.validateEntity();
        final boolean isValidatePhone = gxRequestBodyToEntityAnnotation.isValidatePhone();
        final String phoneFieldName = gxRequestBodyToEntityAnnotation.phoneFieldName();
        final Integer coreModelId = dict.getInt(GXCommonConstants.CORE_MODEL_PRIMARY_NAME);
        for (String jsonField : jsonFields) {
            final String json = Optional.ofNullable(dict.getStr(jsonField)).orElse("{}");
            final Dict targetDict = gxCoreModelAttributesService.getModelAttributesDefaultValue(coreModelId, jsonField, json);
            Dict tmpDict = JSONUtil.toBean(json, Dict.class);
            if (isValidatePhone && tmpDict.containsKey(phoneFieldName)) {
                final String phoneNumber = tmpDict.getStr(phoneFieldName);
                if (GXCommonUtils.checkPhone(phoneNumber)) {
                    throw new GXException(GXResultCode.WRONG_PHONE.getMsg(), GXResultCode.WRONG_PHONE.getCode());
                }
                tmpDict.set(phoneFieldName, GXCommonUtils.encryptedPhoneNumber(phoneNumber));
                targetDict.set(phoneFieldName, GXCommonUtils.encryptedPhoneNumber(phoneNumber));
            }
            final Set<String> tmpDictKey = tmpDict.keySet();
            if (!tmpDict.isEmpty() && !CollUtil.containsAll(targetDict.keySet(), tmpDictKey)) {
                throw new GXException(StrUtil.format("{}字段参数不匹配(系统预置: {} , 实际请求: {})", jsonField, targetDict.keySet(), tmpDictKey), GXResultCode.PARSE_REQUEST_JSON_ERROR.getCode());
            }
            Map<String, Object> filter = filter(targetDict, (Map.Entry<String, Object> t) -> null != tmpDict.getStr(t.getKey()));
            if (fillJSONField && !targetDict.isEmpty()) {
                dict.set(jsonField, JSONUtil.toJsonStr(targetDict));
            } else if (!filter.isEmpty()) {
                dict.set(jsonField, JSONUtil.toJsonStr(filter));
            }
        }
        if (isValidatePhone && dict.containsKey(phoneFieldName)) {
            final String phoneNumber = dict.getStr(phoneFieldName);
            if (GXCommonUtils.checkPhone(phoneNumber)) {
                throw new GXException(GXResultCode.WRONG_PHONE.getMsg(), GXResultCode.WRONG_PHONE.getCode());
            }
            dict.set(phoneFieldName, GXCommonUtils.encryptedPhoneNumber(phoneNumber));
        }
        Object bean = Convert.convert(parameterType, dict);
        Class<?>[] groups = gxRequestBodyToEntityAnnotation.groups();
        if (validateEntity) {
            if (parameter.hasParameterAnnotation(Valid.class)) {
                GXValidatorUtils.validateEntity(bean, value, groups);
            } else if (parameter.hasParameterAnnotation(Validated.class)) {
                groups = Objects.requireNonNull(parameter.getParameterAnnotation(Validated.class)).value();
                GXValidatorUtils.validateEntity(bean, value, groups);
            }
        }
        return bean;
    }

    private String getRequestBody(NativeWebRequest webRequest) {
        HttpServletRequest servletRequest = webRequest.getNativeRequest(HttpServletRequest.class);
        assert servletRequest != null;
        String jsonBody = (String) servletRequest.getAttribute(JSON_REQUEST_BODY);
        if (null == jsonBody) {
            try {
                jsonBody = IoUtil.read(servletRequest.getInputStream(), StandardCharsets.UTF_8);
                servletRequest.setAttribute(JSON_REQUEST_BODY, jsonBody);
            } catch (IOException e) {
                throw new GXException(e.getMessage(), e);
            }
        }
        if (!JSONUtil.isJson(jsonBody)) {
            throw new GXException(GXResultCode.REQUEST_JSON_NOT_BODY);
        }
        return jsonBody;
    }
}
