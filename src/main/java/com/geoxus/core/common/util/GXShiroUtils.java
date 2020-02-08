package com.geoxus.core.common.util;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.oauth.GXTokenManager;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.apache.shiro.subject.Subject;

import java.util.Optional;

public class GXShiroUtils {
    private GXShiroUtils() {
    }

    public static Session getSession() {
        return SecurityUtils.getSubject().getSession();
    }

    public static Subject getSubject() {
        return SecurityUtils.getSubject();
    }

    public static Dict getAdminData() {
        return (Dict) SecurityUtils.getSubject().getPrincipal();
    }

    public static Long getAdminId() {
        return Optional.ofNullable(getAdminData().getLong(GXTokenManager.ADMIN_ID)).orElse(getAdminData().getLong(StrUtil.toCamelCase(GXTokenManager.ADMIN_ID)));
    }

    public static void setSessionAttribute(Object key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static Object getSessionAttribute(Object key) {
        return getSession().getAttribute(key);
    }

    public static boolean isLogin() {
        return SecurityUtils.getSubject().getPrincipal() != null;
    }
}
