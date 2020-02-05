package com.geoxus.core.datasource.config;

import com.alibaba.druid.pool.DruidDataSource;
import com.geoxus.core.datasource.properties.GXDataSourceProperties;
import com.geoxus.core.datasource.properties.GXDynamicDataSourceProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 配置多数据源
 */
@Configuration
public class GXDynamicDataSourceConfig {
    @Autowired
    private GXDynamicDataSourceProperties dynamicDataSourceProperties;

    @Bean
    @ConfigurationProperties(prefix = "spring.datasource.druid")
    public GXDataSourceProperties dataSourceProperties() {
        return new GXDataSourceProperties();
    }

    @Bean
    public GXDynamicDataSource dynamicDataSource(GXDataSourceProperties dataSourceProperties) {
        GXDynamicDataSource dynamicDataSource = new GXDynamicDataSource();
        dynamicDataSource.setTargetDataSources(getDynamicDataSource());
        //默认数据源
        DruidDataSource defaultDataSource = GXDynamicDataSourceFactory.buildDruidDataSource(dataSourceProperties);
        dynamicDataSource.setDefaultTargetDataSource(defaultDataSource);
        return dynamicDataSource;
    }
    
    private Map<Object, Object> getDynamicDataSource() {
        Map<String, GXDataSourceProperties> dataSourcePropertiesMap = dynamicDataSourceProperties.getDatasource();
        Map<Object, Object> targetDataSources = new HashMap<>(dataSourcePropertiesMap.size());
        dataSourcePropertiesMap.forEach((k, v) -> {
            DruidDataSource druidDataSource = GXDynamicDataSourceFactory.buildDruidDataSource(v);
            targetDataSources.put(k, druidDataSource);
        });
        return targetDataSources;
    }
}
