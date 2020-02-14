package com.geoxus.core.common.util;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.oauth.GXTokenManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class GXHttpContextUtils {
    @GXFieldCommentAnnotation(zh = "日志对象")
    private static final Logger LOG = LoggerFactory.getLogger(GXHttpContextUtils.class);

    private GXHttpContextUtils() {
    }

    /**
     * 获取HttpRequest
     *
     * @return
     */
    public static HttpServletRequest getHttpServletRequest() {
        if (null != RequestContextHolder.getRequestAttributes()) {
            return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        }
        return null;
    }

    /**
     * 获取Http请求中的URL
     *
     * @return
     */
    public static String getDomain() {
        HttpServletRequest request = getHttpServletRequest();
        StringBuffer url = Objects.requireNonNull(request).getRequestURL();
        return url.delete(url.length() - request.getRequestURI().length(), url.length()).toString();
    }

    /**
     * 获取Http头中的Origin
     *
     * @return
     */
    public static String getOrigin() {
        HttpServletRequest request = getHttpServletRequest();
        return Objects.requireNonNull(request).getHeader("Origin");
    }

    /**
     * 获取Http请求中的数据
     *
     * @param paramName
     * @param resultType
     * @return
     */
    public static <T> T getHttpParam(String paramName, Class<T> resultType) {
        Object jsonRequestBody = "{}";
        final HttpServletRequest httpServletRequest = getHttpServletRequest();
        if (null != httpServletRequest) {
            jsonRequestBody = Optional.ofNullable(httpServletRequest.getAttribute("JSON_REQUEST_BODY")).orElse("{}");
        }
        final JSONObject jsonObject = JSONUtil.toBean(jsonRequestBody.toString(), JSONObject.class);
        final T value;
        if (!jsonObject.isEmpty()) {
            value = jsonObject.getByPath(paramName, resultType);
        } else {
            assert httpServletRequest != null;
            value = Convert.convert(resultType, httpServletRequest.getParameter(paramName));
        }
        if (null == value) {
            return ReflectUtil.newInstanceIfPossible(resultType);
        }
        return value;
    }

    /**
     * 获取Http头中的header
     *
     * @return
     */
    public static String getHeader(String headerName) {
        HttpServletRequest request = getHttpServletRequest();
        return Objects.requireNonNull(request).getHeader(headerName);
    }

    /**
     * 从token中获取用户ID
     *
     * @param tokenName
     * @param tokenIdName
     * @return
     */
    public static long getUserIdFromToken(String tokenName, String tokenIdName) {
        final String token = ServletUtil.getHeader(Objects.requireNonNull(getHttpServletRequest()), tokenName, CharsetUtil.UTF_8);
        final Map<String, Object> map = GXTokenManager.decodeUserToken(token);
        if (null != map && !map.isEmpty()) {
            return Convert.toLong(map.get(tokenIdName));
        }
        return 0;
    }

    /**
     * 获取客户端IP
     *
     * @return
     */
    public static String getIP(HttpServletRequest httpServletRequest) {
        String ip = "";
        if (null != httpServletRequest) {
            ip = ServletUtil.getClientIP(httpServletRequest);
        }
        return ip;
    }

    /**
     * 获取客户端IP
     *
     * @return
     */
    public static String getIP() {
        String ip = "";
        if (null != GXHttpContextUtils.getHttpServletRequest()) {
            ip = ServletUtil.getClientIP(GXHttpContextUtils.getHttpServletRequest());
        }
        return ip;
    }
}