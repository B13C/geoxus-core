package com.geoxus.core.framework.config;

import cn.hutool.core.lang.Dict;
import com.geoxus.core.common.util.GXGuavaUtils;
import com.geoxus.core.framework.entity.GXCoreModelEntity;
import com.google.common.cache.Cache;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
public class GXGuavaConfig {
    @Bean
    public Cache<String, String> captchaCache() {
        return GXGuavaUtils.getGuavaCacheExpireAfterWrite(10000, 300, TimeUnit.SECONDS);
    }
    
    @Bean
    public Cache<String, GXCoreModelEntity> coreModelEntityCache() {
        return GXGuavaUtils.getGuavaCacheExpireAfterWrite(10000, 24, TimeUnit.HOURS);
    }

    @Bean
    public Cache<String, Dict> coreModelAttributesDictCache() {
        return GXGuavaUtils.getGuavaCacheExpireAfterWrite(10000, 24, TimeUnit.HOURS);
    }

    @Bean
    public Cache<String, Object> generalGuavaCache() {
        return GXGuavaUtils.getGuavaCacheExpireAfterWrite(10000, 24, TimeUnit.HOURS);
    }
}
