package com.geoxus.core.common.annotation;

import java.lang.annotation.*;

/**
 * 合并指定的属性到数据库配置的JSON字段
 * <p>
 * 该注解需要配合{@code @TableField(exists=false)}使用
 */
@Documented
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface GXMergeSingleFieldToJSONFieldAnnotation {
    String dbJSONFieldFieldName() default "ext";

    String dbFieldName() default "";
}
