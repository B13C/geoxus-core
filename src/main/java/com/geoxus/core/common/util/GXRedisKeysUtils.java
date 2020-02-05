package com.geoxus.core.common.util;

import com.geoxus.core.common.factory.GXYamlPropertySourceFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * Redis所有Keys
 */
@Component
@Slf4j
public class GXRedisKeysUtils {
    @Autowired
    private RedisKeysConfig redisKeysConfig;

    /**
     * 获取系统配置的KEY
     *
     * @param key 缓存KEY
     * @return String
     */
    public String getSysConfigKey(String key) {
        return getRedisKey("sys.config", key);
    }

    /**
     * 获取图形验证码的KEY
     *
     * @param key 缓存KEY
     * @return String
     */
    public String getCaptchaConfigKey(String key) {
        return getRedisKey("sys.captcha", key);
    }

    /**
     * 获取手机验证码的KEY(网易云)
     *
     * @param key 缓存KEY
     * @return String
     */
    public String getNetEaseSMSCodeConfigKey(String key) {
        return getRedisKey("sys.net-ease", key);
    }

    /**
     * 获取手机验证码的KEY(阿里云)
     *
     * @param key 缓存KEY
     * @return String
     */
    public String getAliYunSMSCodeConfigKey(String key) {
        return getRedisKey("sys.ali-yun", key);
    }

    /**
     * 根据配置文件的name和key获取Redis的key
     *
     * @param configName
     * @param key
     * @return
     */
    public String getRedisKey(String configName, String key) {
        final List<Map<String, String>> list = redisKeysConfig.getKeys();
        log.info(list.toString());
        for (Map<String, String> map : list) {
            final String s = map.get(configName);
            if (null != s && !s.isEmpty()) {
                return s + ":" + key;
            }
        }
        return "default:key";
    }

    @Data
    @Component
    @Configuration
    @PropertySource(value = {"classpath:/ymls/${spring.profiles.active}/redis-key.yml"}, factory = GXYamlPropertySourceFactory.class, encoding = "UTF-8")
    @ConfigurationProperties(prefix = "redis-key")
    private static class RedisKeysConfig {
        private List<Map<String, String>> keys;
    }
}
