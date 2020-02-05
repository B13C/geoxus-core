package com.geoxus.core.common.config;

import com.geoxus.core.common.factory.GXYamlPropertySourceFactory;
import com.geoxus.core.common.interceptor.GXAuthorizationInterceptor;
import com.geoxus.core.common.interceptor.GXLoginUserHandlerMethodArgumentResolver;
import com.geoxus.core.common.interceptor.GXRequestToBeanHandlerMethodArgumentResolver;
import com.geoxus.core.ueditor.config.GXEditorProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.CacheControl;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * WEB MVC配置
 */
@Configuration
@Slf4j
public class GXWebMvcInterceptorConfig implements WebMvcConfigurer {
    @Autowired
    private UploadConfig uploadConfig;

    @Autowired
    private GXAuthorizationInterceptor authorizationInterceptor;

    @Autowired
    private GXLoginUserHandlerMethodArgumentResolver loginUserHandlerMethodArgumentResolver;

    @Autowired
    private GXRequestToBeanHandlerMethodArgumentResolver requestToBeanHandlerMethodArgumentResolver;

    @Autowired
    private WebMvcConfig webMvcConfig;

    @Autowired
    private GXEditorProperties properties;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        final List<String> list = webMvcConfig.getUrlPatterns();
        registry.addInterceptor(authorizationInterceptor).addPathPatterns(list);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(loginUserHandlerMethodArgumentResolver);
        argumentResolvers.add(requestToBeanHandlerMethodArgumentResolver);
    }

    /**
     * 对文件的路径进行配置,创建一个虚拟路径/Path/** ，即只要在<img src="/Path/picName.jpg"/>便可以直接引用图片
     * 这是图片的物理路径  "file:/+本地图片的地址"
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        try {
            final List<String> list = webMvcConfig.getResourcePatterns();
            String[] array = new String[list.size()];
            list.toArray(array);
            registry.addResourceHandler(array)
                    .addResourceLocations("file:" + ResourceUtils.getURL(uploadConfig.getDepositPath()).getPath())
                    //PHP图片地址
                    .addResourceLocations("file:" + ResourceUtils.getURL(uploadConfig.getDepositPath() + File.separator + "media").getPath());
            //编辑器上传路径
            registry.addResourceHandler(properties.getLocal().getUrlPrefix() + "**")
                    .addResourceLocations("file:" + ResourceUtils.getURL(properties.getLocal().getPhysicalPath()).getPath())
                    .setCacheControl(CacheControl.maxAge(20, TimeUnit.DAYS));
        } catch (Exception e) {
            log.error("虚拟路径配置错误", e);
        }
    }

    @Data
    @Component
    @ConfigurationProperties(prefix = "web-mvc")
    @PropertySource(value = {"classpath:/ymls/${spring.profiles.active}/web-mvc.yml"}, factory = GXYamlPropertySourceFactory.class, encoding = "utf-8")
    private static class WebMvcConfig {
        private List<String> urlPatterns;
        private List<String> resourcePatterns;
    }
}
