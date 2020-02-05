package com.geoxus.core.common.config;

import com.geoxus.core.common.factory.GXYamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "wechat", ignoreUnknownFields = false)
@PropertySource(value = "classpath:/ymls/${spring.profiles.active}/wechat.yml", factory = GXYamlPropertySourceFactory.class)
public class GXWeChatConfig {

    private String appId;

    private String token;

    private String appSecret;

    private String aesKey;

    private String mchId;

    private String mchKey;

    private String keyPath;

    private String notifyUrl;

    private String certLocalPath;

    private String certRootPath;

    private String subject;

    private String body;
}
