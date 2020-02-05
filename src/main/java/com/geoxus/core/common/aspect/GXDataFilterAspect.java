package com.geoxus.core.common.aspect;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.core.lang.TypeReference;
import com.geoxus.core.common.annotation.GXDataFilterAnnotation;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.exception.GXException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 数据过滤，切面处理类
 */
@Aspect
@Component
public class GXDataFilterAspect {
    @GXFieldCommentAnnotation(zh = "数据权限过滤")
    private static final String SQL_FILTER = "sql_filter";

    @Pointcut("@annotation(com.geoxus.core.common.annotation.GXDataFilterAnnotation)")
    public void dataFilterCut() {
    }

    @Before("dataFilterCut()")
    public void dataFilter(JoinPoint point) {
        Object params = point.getArgs()[0];
        if (params instanceof Map) {
            Map<String, Object> map = Convert.convert(new TypeReference<Map<String, Object>>() {
            }, params);
            map.put(SQL_FILTER, getSQLFilter(Dict.create(), point));
            return;
        }
        throw new GXException("数据权限接口，只能是Map类型参数，且不能为NULL");
    }

    /**
     * 获取数据过滤的SQL
     */
    private String getSQLFilter(Dict param, JoinPoint point) {
        MethodSignature signature = (MethodSignature) point.getSignature();
        GXDataFilterAnnotation dataFilterAnnotation = signature.getMethod().getAnnotation(GXDataFilterAnnotation.class);
        StringBuilder sqlFilter = new StringBuilder();
        sqlFilter.append(" (");
        sqlFilter.append(")");
        if (sqlFilter.toString().trim().equals("()")) {
            return null;
        }
        return sqlFilter.toString();
    }
}
