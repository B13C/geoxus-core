package com.geoxus.core.common.oauth;

import cn.hutool.core.lang.Dict;
import cn.hutool.core.util.StrUtil;
import com.geoxus.core.common.service.GXShiroService;
import com.geoxus.core.common.vo.GXBusinessStatusCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GXOAuth2Realm extends AuthorizingRealm {
    @Autowired
    private GXShiroService shiroService;

    @Override
    public boolean supports(AuthenticationToken token) {
        return token instanceof GXOAuth2Token;
    }

    /**
     * 授权(验证权限时调用)
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        log.info("授权验证权限时调用-->OAuth2Realm.doGetAuthorizationInfo() principals : " + principals.getPrimaryPrincipal());
        Dict dict = (Dict) principals.getPrimaryPrincipal();
        Long adminId = Optional.ofNullable(dict.getLong(GXTokenManager.ADMIN_ID)).orElse(dict.getLong(StrUtil.toCamelCase(GXTokenManager.ADMIN_ID)));
        //用户权限列表
        Set<String> permsSet = shiroService.getAdminAllPermissions(adminId);
        //用户角色
        Set<String> rolesSet = shiroService.getAdminRoles(adminId).values().stream().map(Object::toString).collect(Collectors.toSet());
        SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
        info.setStringPermissions(permsSet);
        info.addRoles(rolesSet);
        return info;
    }

    /**
     * 认证(登录时调用)
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
        log.info("认证 ---> OAuth2Realm.doGetAuthenticationInfo()" + token.getPrincipal() + " : " + token.getCredentials());
        String accessToken = token.getPrincipal().toString();
        // 根据accessToken,获取token中的值
        Dict dict = GXTokenManager.decodeAdminToken(accessToken);
        // 判断token是否失效
        if (null == dict || dict.isEmpty()) {
            throw new IncorrectCredentialsException("长时间未操作，请重新登录");
        }
        // 从TOKEN中获取用户ID
        Long adminId = dict.getLong(GXTokenManager.ADMIN_ID);
        if (null == adminId) {
            throw new IncorrectCredentialsException("请提供正确的字段");
        }
        //根据用户ID查询用户信息
        Dict admin = shiroService.getAdminById(adminId);
        //判断账号的状态
        int userStatus = admin.getInt("status");
        if (userStatus == GXBusinessStatusCode.FREEZE.getCode()) {
            throw new LockedAccountException(GXBusinessStatusCode.FREEZE.getMsg());
        }
        return new SimpleAuthenticationInfo(admin, accessToken, getName());
    }

    @Override
    public boolean isPermitted(PrincipalCollection principals, String permission) {
        Dict admin = (Dict) principals.getPrimaryPrincipal();
        return shiroService.isSuperAdmin(admin) || super.isPermitted(principals, permission);
    }

    @Override
    public boolean hasRole(PrincipalCollection principals, String roleIdentifier) {
        Dict admin = (Dict) principals.getPrimaryPrincipal();
        return shiroService.isSuperAdmin(admin) || super.hasRole(principals, roleIdentifier);
    }
}
