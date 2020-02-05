package com.geoxus.core.common.vo.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.geoxus.core.common.annotation.GXFieldCommentAnnotation;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class GXPagination implements Serializable {
    private static final long serialVersionUID = 1L;

    @GXFieldCommentAnnotation(zh = "总记录数")
    private long total;

    @GXFieldCommentAnnotation(zh = "每页记录数")
    private long size;

    @GXFieldCommentAnnotation(zh = "总页数")
    private long pages;

    @GXFieldCommentAnnotation(zh = "当前页数")
    private long current;

    @GXFieldCommentAnnotation(zh = "列表数据")
    private List<?> records;

    /**
     * 分页
     *
     * @param list       列表数据
     * @param totalCount 总记录数
     * @param pageSize   每页记录数
     * @param currPage   当前页数
     */
    public GXPagination(List<?> list, long totalCount, long pageSize, long currPage) {
        this.records = list;
        this.total = totalCount;
        this.size = pageSize;
        this.current = currPage;
        this.pages = (int) Math.ceil((double) totalCount / pageSize);
    }

    /**
     * 分页
     */
    public GXPagination(Page<?> page) {
        this.records = page.getRecords();
        this.total = page.getTotal();
        this.size = page.getSize();
        this.current = page.getCurrent();
        this.pages = page.getPages();
    }

    /**
     * 分页
     */
    public GXPagination(IPage<?> page) {
        this.records = page.getRecords();
        this.total = (int) page.getTotal();
        this.size = (int) page.getSize();
        this.current = (int) page.getCurrent();
        this.pages = (int) page.getPages();
    }

    /**
     * 分页
     */
    public GXPagination(List<?> list) {
        this.records = list;
    }
}
