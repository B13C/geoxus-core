package com.geoxus.core.common.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.TypeReference;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.handlers.MybatisMapWrapper;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.BlockAttackInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.OptimisticLockerInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.wrapper.ObjectWrapper;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.util.Map;

@Slf4j
@EnableTransactionManagement
@Configuration
public class GXMyBatisPlusConfig {
    private static void customize(org.apache.ibatis.session.Configuration configuration) {
        configuration.setObjectWrapperFactory(new ObjectWrapperFactory() {
            @Override
            public boolean hasWrapperFor(Object object) {
                return object instanceof Map;
            }

            @Override
            public ObjectWrapper getWrapperFor(MetaObject metaObject, Object object) {
                final Map<String, Object> map = Convert.convert(new TypeReference<Map<String, Object>>() {
                }, object);
                return new MybatisMapWrapper(metaObject, map) {
                    @Override
                    public String findProperty(String name, boolean useCamelCaseMapping) {
                        if (useCamelCaseMapping && !StringUtils.isCamel(name)) {
                            return StringUtils.underlineToCamel(name);
                        }
                        return name;
                    }
                };
            }
        });
    }

    @Bean
    public MybatisPlusInterceptor paginationInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        // 攻击 SQL 阻断解析器,防止全表更新与删除
        interceptor.addInnerInterceptor(new BlockAttackInnerInterceptor());
        // 乐观锁
        interceptor.addInnerInterceptor(new OptimisticLockerInnerInterceptor());
        return interceptor;
    }

    @Bean
    @ConditionalOnExpression("'${use-camel-case-mapping}'.equals('true')")
    public ConfigurationCustomizer configurationCustomizer() {
        return GXMyBatisPlusConfig::customize;
    }
}
