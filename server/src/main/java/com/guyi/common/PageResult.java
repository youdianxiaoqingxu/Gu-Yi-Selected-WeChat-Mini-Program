package com.guyi.common;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResult<T> {

    private List<T> list;
    private long total;
    private long page;
    private long size;

    public PageResult() {
    }

    public PageResult(List<T> list, long total, long page, long size) {
        this.list = list;
        this.total = total;
        this.page = page;
        this.size = size;
    }
}

