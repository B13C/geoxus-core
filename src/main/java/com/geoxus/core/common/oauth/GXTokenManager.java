package com.geoxus.core.common.oauth;

import cn.hutool.core.lang.Dict;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONException;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.util.GXAuthCodeUtils;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;

import java.time.Duration;
import java.util.concurrent.ExecutionException;

@Slf4j
public class GXTokenManager {
    @GXFieldCommentAnnotation(zh = "TOKEN过期时间")
    public static final int EXPIRES = 24 * 60 * 60 * 15;

    @GXFieldCommentAnnotation(zh = "USER端TOKEN即将过期刷新时间")
    public static final int USER_EXPIRES_REFRESH = 60 * 60 * 7;

    @GXFieldCommentAnnotation(zh = "Admin登录用的token标签")
    public static final String ADMIN_ID = "admin_id";

    @GXFieldCommentAnnotation(zh = "用户登录用的token标签")
    public static final String USER_ID = "user_id";

    @GXFieldCommentAnnotation(zh = "管理员token的名字")
    public static final String ADMIN_TOKEN = "admin-token";

    @GXFieldCommentAnnotation(zh = "用户token的名字")
    public static final String USER_TOKEN = "user-token";

    @GXFieldCommentAnnotation(zh = "TOKEN加密的KEY")
    private static final String KEY = "GEO_XUS_SHIRO_TOKEN_KEY";

    @GXFieldCommentAnnotation(zh = "ADMIN端TOKEN即将过期的刷新时间")
    private static final int ADMIN_EXPIRES_REFRESH = 24 * 60;

    @GXFieldCommentAnnotation(zh = "管理员的token缓存组件")
    private static final Cache<String, Dict> ADMIN_TOKEN_CACHE = CacheBuilder.newBuilder().maximumSize(10000).expireAfterAccess(Duration.ofMinutes(24)).build();

    private GXTokenManager() {
    }

    /**
     * 生成ADMIN后台登录的token
     *
     * <pre>
     *     {@code
     *     generateUserToken(1 , Dict.create().set("phone" , "13800138000"))
     *     }
     * </pre>
     *
     * @param adminId 管理员ID
     * @param param   附加信息
     * @return String
     */
    public static String generateAdminToken(long adminId, Dict param) {
        param.putIfAbsent(ADMIN_ID, adminId);
        param.put("platform", "GEO_XUS");
        return GXAuthCodeUtils.authCodeEncode(JSONUtil.toJsonStr(param), KEY, ADMIN_EXPIRES_REFRESH);
    }

    /**
     * 生成前端用户的登录Token
     *
     * <pre>
     *     {@code
     *     generateUserToken(1 , Dict.create().set("phone" , "13800138000"))
     *     }
     * </pre>
     *
     * @param userId 前端用户的ID
     * @param param  附加信息
     * @return String
     */
    public static String generateUserToken(long userId, Dict param) {
        param.putIfAbsent(USER_ID, userId);
        return GXAuthCodeUtils.authCodeEncode(JSONUtil.toJsonStr(param), KEY);
    }

    /**
     * 解码前端用户的TOKEN字符串
     * 注意: 由于前端用户需要保持长时间的登录信息,在生成token字符串时,不需要指定token的过期时间,过期时间时存放在s_user_token表中的
     *
     * @param source 加密TOKEN字符串
     * @return Dict
     */
    public static Dict decodeUserToken(String source) {
        try {
            String s = GXAuthCodeUtils.authCodeDecode(source, KEY);
            return JSONUtil.toBean(s, Dict.class);
        } catch (Exception e) {
            return Dict.create();
        }
    }

    /**
     * 解码后端用户的加密TOKEN字符串
     * 注意: 由于后端用户不需要保持长时间的登录操作,所以在生成token时,为token指定了过期时间
     *
     * @param source 加密TOKEN字符串
     * @return Dict
     */
    public static Dict decodeAdminToken(String source) {
        try {
            String cacheKey = SecureUtil.md5(source);
            return ADMIN_TOKEN_CACHE.get(cacheKey, () -> {
                String s = GXAuthCodeUtils.authCodeDecode(source, KEY);
                Dict dict = JSONUtil.toBean(s, Dict.class);
                if (dict.isEmpty()) {
                    return Dict.create();
                }
                return dict;
            });
        } catch (JSONException | ExecutionException exception) {
            log.error(exception.getMessage());
        }
        return Dict.create();
    }
}
