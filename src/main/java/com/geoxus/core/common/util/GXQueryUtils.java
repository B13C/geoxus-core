package com.geoxus.core.common.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import com.geoxus.core.common.filter.GXSQLFilter;

import java.util.Map;

/**
 * 查询参数
 */
public class GXQueryUtils<T> {
    @GXFieldCommentAnnotation(zh = "当前页码")
    private static final String PAGE = "page";

    @GXFieldCommentAnnotation(zh = "每页显示记录数")
    private static final String LIMIT = "limit";

    @GXFieldCommentAnnotation(zh = "排序字段")
    private static final String ORDER_FIELD = "sidx";

    @GXFieldCommentAnnotation(zh = "排序方式")
    private static final String ORDER = "order";

    @GXFieldCommentAnnotation(zh = "升序")
    private static final String ASC = "asc";

    public IPage<T> getPage(Map<String, Object> params) {
        return this.getPage(params, null, false);
    }

    public IPage<T> getPage(Map<String, Object> params, String defaultOrderField, boolean isAsc) {
        long curPage = 1;
        long limit = 10;
        if (params.get(PAGE) != null) {
            curPage = Long.parseLong((String) params.get(PAGE));
        }
        if (params.get(LIMIT) != null) {
            limit = Long.parseLong((String) params.get(LIMIT));
        }

        Page<T> page = new Page<>(curPage, limit);

        params.put(PAGE, page);

        String orderField = GXSQLFilter.sqlInject((String) params.get(ORDER_FIELD));
        String order = (String) params.get(ORDER);

        if (StrUtil.isNotEmpty(orderField) && StrUtil.isNotEmpty(order)) {
            if (ASC.equalsIgnoreCase(order)) {
                return page.addOrder(OrderItem.asc(orderField));
            } else {
                return page.addOrder(OrderItem.desc(orderField));
            }
        }

        if (StrUtil.isBlank(defaultOrderField)) {
            return page;
        }

        if (isAsc) {
            page.addOrder(OrderItem.asc(defaultOrderField));
        } else {
            page.addOrder(OrderItem.desc(defaultOrderField));
        }
        return page;
    }
}
