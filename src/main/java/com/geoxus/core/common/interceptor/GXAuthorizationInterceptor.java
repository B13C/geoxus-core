package com.geoxus.core.common.interceptor;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.annotation.GXLoginAnnotation;
import com.geoxus.core.common.exception.GXException;
import com.geoxus.core.common.exception.GXTokenEmptyException;
import com.geoxus.core.common.oauth.GXTokenManager;
import com.geoxus.core.common.service.GXUUserService;
import com.geoxus.core.common.util.GXSpringContextUtils;
import com.geoxus.core.common.vo.GXResultCode;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Objects;

/**
 * 前端用户Token验证
 */
@Component
public class GXAuthorizationInterceptor extends HandlerInterceptorAdapter {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        GXLoginAnnotation annotation;
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return true;
        }
        if (handler instanceof HandlerMethod) {
            annotation = ((HandlerMethod) handler).getMethodAnnotation(GXLoginAnnotation.class);
        } else {
            return true;
        }
        if (annotation == null) {
            return true;
        }

        //获取用户凭证
        String token = request.getHeader(GXTokenManager.USER_TOKEN);
        if (StrUtil.isBlank(token)) {
            token = request.getParameter(GXTokenManager.USER_TOKEN);
        }
        //凭证为空
        if (StrUtil.isBlank(token)) {
            throw new GXTokenEmptyException("请先进行登录", HttpStatus.UNAUTHORIZED.value());
        }

        Dict dict = Objects.requireNonNull(GXSpringContextUtils.getBean(GXUUserService.class)).verifyUserToken(token);
        if (dict == null || dict.isEmpty()) {
            throw new GXException(GXResultCode.TOKEN_TIMEOUT_EXIT.getMsg(), HttpStatus.UNAUTHORIZED.value());
        }
        //设置userId到request里，后续根据userId，获取用户信息
        request.setAttribute(GXTokenManager.USER_ID, dict.get(GXTokenManager.USER_ID));

        return true;
    }
}
