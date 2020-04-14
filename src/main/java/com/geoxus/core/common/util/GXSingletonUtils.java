package com.geoxus.core.common.util;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.spring.cache.RedissonSpringCacheManager;
import org.springframework.cache.CacheManager;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;

import java.net.URL;

public class GXSingletonUtils {
    /**
     * 获取EhCacheCacheManager
     *
     * @return EhCacheCacheManager
     */
    public static EhCacheCacheManager getEhCacheCacheManager() {
        final String beanName = "a1cddb7233521d";
        EhCacheCacheManager ehCacheCacheManager = GXSpringContextUtils.getBean(EhCacheCacheManager.class);
        if (null != ehCacheCacheManager) {
            return ehCacheCacheManager;
        }
        ehCacheCacheManager = GXSpringContextUtils.getBean(beanName, EhCacheCacheManager.class);
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
        EhCacheCacheManager manager = new EhCacheCacheManager(cacheManager);
        GXCommonUtils.registerSingleton(beanName, manager);
        return manager;
    }

    /**
     * 获取RedissonSpringCacheManager
     *
     * @return RedissonSpringCacheManager
     */
    public static RedissonSpringCacheManager getRedissonSpringCacheManager() {
        final String beanName = "9372fd46c598a";
        final CacheManager cacheManager = GXSpringContextUtils.getBean(CacheManager.class);
        if (cacheManager instanceof org.redisson.spring.cache.RedissonSpringCacheManager) {
            return (RedissonSpringCacheManager) cacheManager;
        }
        RedissonSpringCacheManager bean = GXSpringContextUtils.getBean(beanName, RedissonSpringCacheManager.class);
        if (null != bean) {
            return bean;
        }
        try {
            URL resource = GXCommonUtils.class.getClassLoader().getResource("redisson.yml");
            Config config = Config.fromYAML(resource);
            RedissonClient redissonClient = Redisson.create(config);
            RedissonSpringCacheManager manager = new RedissonSpringCacheManager(redissonClient, "classpath:/redisson-cache-config.yml");
            manager.setResourceLoader(getDefaultResourceLoader());
            manager.afterPropertiesSet();
            GXCommonUtils.registerSingleton(beanName, manager);
            return manager;
        } catch (Exception e) {
            GXCommonUtils.getLogger(GXSignatureUtils.class).error("读取redisson.yml文件失败...");
        }
        return null;
    }

    /**
     * 获取Spring的DefaultResourceLoader
     *
     * @return DefaultResourceLoader
     */
    public static DefaultResourceLoader getDefaultResourceLoader() {
        final String beanName = "6C598A1CDDB7233";
        DefaultResourceLoader bean = GXSpringContextUtils.getBean(beanName, DefaultResourceLoader.class);
        if (null != bean) {
            return bean;
        }
        DefaultResourceLoader loader = new DefaultResourceLoader();
        GXCommonUtils.registerSingleton(beanName, loader);
        return loader;
    }
}
