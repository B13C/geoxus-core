package com.geoxus.core.common.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.geoxus.core.common.factory.GXYamlPropertySourceFactory;
import com.geoxus.core.common.interceptor.GXCustomMultipartResolver;
import com.geoxus.core.common.validator.impl.GXValidateDBUniqueValidator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.MultipartResolver;

import javax.servlet.MultipartConfigElement;
import java.io.IOException;

/**
 * 通用配置类
 */
@Configuration
@PropertySource(value = "classpath:/ymls/${spring.profiles.active}/common.yml", factory = GXYamlPropertySourceFactory.class)
public class GXCommonConfig {
    @Bean
    public GXValidateDBUniqueValidator validateDBUniqueOrExistsValidator() {
        return new GXValidateDBUniqueValidator();
    }

    @Bean
    @ConditionalOnExpression("'${enable-fileupload-progress}'.equals('false')")
    public MultipartConfigElement multipartConfigElement() {
        MultipartConfigFactory factory = new MultipartConfigFactory();
        factory.setMaxFileSize(DataSize.ofMegabytes(10L));
        factory.setMaxRequestSize(DataSize.ofMegabytes(20L));
        return factory.createMultipartConfig();
    }

    @Bean
    @ConditionalOnExpression("'${enable-fileupload-progress}'.equals('true')")
    public MultipartResolver multipartResolver() {
        return new GXCustomMultipartResolver();
    }

    @Bean
    @ConditionalOnMissingBean(ObjectMapper.class)
    public ObjectMapper jacksonObjectMapper(Jackson2ObjectMapperBuilder builder) {
        final ObjectMapper objectMapper = builder.createXmlMapper(false).build();
        final SerializerProvider serializerProvider = objectMapper.getSerializerProvider();
        serializerProvider.setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
                gen.writeString("");
            }
        });
        return objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
    }
}
