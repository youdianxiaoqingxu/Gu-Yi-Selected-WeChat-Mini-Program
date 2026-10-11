package com.guyi.common;

public class PageQuery {

    private long page = 1;
    private long size = 10;

    public long getPage() {
        return page < 1 ? 1 : page;
    }

    public void setPage(long page) {
        this.page = page;
    }

    public long getSize() {
        if (size < 1) {
            return 10;
        }
        return Math.min(size, 50);
    }

    public void setSize(long size) {
        this.size = size;
    }
}

