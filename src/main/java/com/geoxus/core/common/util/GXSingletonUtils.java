package com.geoxus.core.common.util;

import cn.hutool.core.lang.Dict;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;

import java.net.URL;

public enum GXSingletonUtils {
    EH_CACHE_CACHE_MANAGER_INSTANCE(Dict.create()) {
        @Override
        public Object getInstance() {
            EhCacheCacheManager ehCacheCacheManager = GXSpringContextUtils.getBean(EhCacheCacheManager.class);
            if (null != ehCacheCacheManager) {
                return ehCacheCacheManager;
            }
            EhCacheManagerFactoryBean ehCacheManagerFactoryBean = new EhCacheManagerFactoryBean();
            ehCacheManagerFactoryBean.setConfigLocation(new ClassPathResource("ehcache.xml"));
            ehCacheManagerFactoryBean.setShared(true);
            net.sf.ehcache.CacheManager cacheManager = ehCacheManagerFactoryBean.getObject();
            if (null == cacheManager) {
                cacheManager = new net.sf.ehcache.CacheManager();
            }
            return new EhCacheCacheManager(cacheManager);
        }
    },
    REDISSON_SPRING_CACHE_MANAGER(Dict.create()) {
        @Override
        public Object getInstance() {
            final CacheManager cacheManager = GXSpringContextUtils.getBean(CacheManager.class);
            if (cacheManager instanceof org.redisson.spring.cache.RedissonSpringCacheManager) {
                return cacheManager;
            }
            try {
                URL resource = GXCommonUtils.class.getClassLoader().getResource("redisson.yml");
                Config config = Config.fromYAML(resource);
                RedissonClient redissonClient = Redisson.create(config);
                RedissonSpringCacheManager manager = new RedissonSpringCacheManager(redissonClient, "classpath:/redisson-cache-config.yml");
                manager.setResourceLoader((ResourceLoader) GXSingletonUtils.DEFAULT_RESOURCE_LOADER.getInstance());
                manager.afterPropertiesSet();
                return manager;
            } catch (Exception e) {
                GXCommonUtils.getLogger(GXSignatureUtils.class).error("读取redisson.yml文件失败...");
            }
            return null;
        }
    },
    DEFAULT_RESOURCE_LOADER(Dict.create()) {
        @Override
        public Object getInstance() {
            return new DefaultResourceLoader();
        }
    };

    private Dict param;

    GXSingletonUtils(Dict param) {
    }

    public abstract Object getInstance();
}
