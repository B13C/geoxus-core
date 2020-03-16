package com.geoxus.core.common.util;

import org.slf4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.env.Environment;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Spring Context 工具类
 */
@Component
public class GXSpringContextUtils implements ApplicationContextAware {
    private static final Logger log = GXCommonUtils.getLogger(GXSpringContextUtils.class);

    private static ApplicationContext applicationContext;

    public static Object getBean(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (NoSuchBeanDefinitionException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static <T> T getBean(String name, Class<T> requiredType) {
        try {
            return applicationContext.getBean(name, requiredType);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static boolean containsBean(String name) {
        return applicationContext.containsBean(name);
    }

    public static boolean isSingleton(String name) {
        return applicationContext.isSingleton(name);
    }

    public static Class<?> getType(String name) {
        return applicationContext.getType(name);
    }

    public static <T> Map<String, T> getBeans(Class<T> clazz) {
        return applicationContext.getBeansOfType(clazz);
    }

    public static Environment getEnvironment() {
        return applicationContext.getEnvironment();
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        GXSpringContextUtils.applicationContext = applicationContext;
    }
}
