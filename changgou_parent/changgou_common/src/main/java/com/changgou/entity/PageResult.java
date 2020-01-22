package com.changgou.entity;
import java.util.List;
public class PageResult<T> {

    private Long total;//总记录数
    private Integer pageTotal; //总页数
    private List<T> rows;//记录

    public PageResult(Long total, List<T> rows) {
        this.total = total;
        this.rows = rows;
    }

    public PageResult(Long total, Integer pageTotal, List<T> rows) {
        this.total = total;
        this.pageTotal = pageTotal;
        this.rows = rows;
    }

    public PageResult() {
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }

    public List<T> getRows() {
        return rows;
    }

    public void setRows(List<T> rows) {
        this.rows = rows;
    }
}
