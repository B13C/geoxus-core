package com.geoxus.core.common.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.annotation.GXRequestBodyToBeanAnnotation;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.validator.impl.GXValidatorUtils;
import com.geoxus.core.common.vo.GXResultCode;
import org.springframework.core.MethodParameter;
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
import java.util.Objects;
import java.util.Optional;

@Component
public class GXRequestToBeanHandlerMethodArgumentResolver implements HandlerMethodArgumentResolver {
    @GXFieldCommentAnnotation(zh = "请求中的参数名字")
    public static final String JSON_REQUEST_BODY = "JSON_REQUEST_BODY";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(GXRequestBodyToBeanAnnotation.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        final String body = getRequestBody(webRequest);
        if (!JSONUtil.isJson(body)) {
            throw new GXException(GXResultCode.NEED_JSON_FORMAT);
        }
        final Dict dict = Convert.convert(Dict.class, JSONUtil.toBean(body, Dict.class));
        if (dict.isEmpty()) {
            throw new GXException(GXResultCode.REQUEST_JSON_NOT_BODY);
        }
        final Class<?> parameterType = parameter.getParameterType();
        final GXRequestBodyToBeanAnnotation gxRequestBodyToBeanAnnotation = parameter.getParameterAnnotation(GXRequestBodyToBeanAnnotation.class);
        final String value = Objects.requireNonNull(gxRequestBodyToBeanAnnotation).value();
        Object bean;
        if (StrUtil.isNotBlank(value)) {
            final Object o = JSONUtil.getByPath(JSONUtil.parseObj(body), value);
            if (o instanceof JSONArray) {
                bean = Convert.convert(parameterType, o);
            } else if (JSONUtil.isJsonArray(o.toString())) {
                final JSONArray jsonArray = JSONUtil.parseArray(o);
                bean = Convert.toList(parameterType, jsonArray);
            } else {
                final JSONObject jsonObject = Convert.convert(JSONObject.class, o);
                if (null == jsonObject) {
                    throw new GXException(StrUtil.format("请求参数没有{}键", value), GXResultCode.PARSE_REQUEST_JSON_ERROR.getCode());
                }
                bean = jsonObject.toBean(parameterType);
            }
        } else {
            bean = Convert.convert(parameterType, dict);
        }
        final boolean enableValidateEntity = (boolean) Optional.ofNullable(ReflectUtil.getFieldValue(bean, "enableValidateEntity")).orElse(true);
        Class<?>[] groups = gxRequestBodyToBeanAnnotation.groups();
        if (parameter.hasParameterAnnotation(Valid.class) && enableValidateEntity) {
            GXValidatorUtils.validateEntity(bean, value, groups);
        }
        if (parameter.hasParameterAnnotation(Validated.class) && enableValidateEntity) {
            groups = Objects.requireNonNull(parameter.getParameterAnnotation(Validated.class)).value();
            GXValidatorUtils.validateEntity(bean, value, groups);
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
        return jsonBody;
    }
}
