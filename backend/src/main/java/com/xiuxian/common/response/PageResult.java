package com.xiuxian.common.response;

import java.util.List;

/**
 * 分页响应对象
 *
 * @author CodeGenerator
 * @date 2026-01-13
 */
public class PageResult<T> {
    /**
     * 数据列表
     */
    private List<T> items;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 当前页码
     */
    private Integer page;

    /**
     * 每页大小
     */
    private Integer pageSize;

    /**
     * 总页数
     */
    private Integer totalPages;

    public PageResult(List<T> items, Long total, Integer page, Integer pageSize) {
        this.items = items;
        this.total = total;
        this.page = page;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
    }

    /**
     * 静态工厂方法
     */
    public static <T> PageResult<T> of(List<T> items, long total, int page, int pageSize) {
        return new PageResult<>(items, total, page, pageSize);
    }

    /**
     * 空结果
     */
    public static <T> PageResult<T> empty(Integer page, Integer pageSize) {
        return new PageResult<>(List.of(), 0L, page, pageSize);
    }

    // Getter and Setter methods
    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public Integer getPage() {
        return page;
    }

    public void setPage(Integer page) {
        this.page = page;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Integer totalPages) {
        this.totalPages = totalPages;
    }
}
