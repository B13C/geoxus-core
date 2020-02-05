package com.geoxus.core.datasource.properties;


import com.geoxus.core.common.factory.GXYamlPropertySourceFactory;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 多数据源属性
 */
@Data
@Component
@PropertySource(value = {"classpath:/ymls/${spring.profiles.active}/datasource.yml"}, factory = GXYamlPropertySourceFactory.class)
@ConfigurationProperties(prefix = "dynamic")
public class GXDynamicDataSourceProperties {
    private Map<String, GXDataSourceProperties> datasource = new LinkedHashMap<>();
}
