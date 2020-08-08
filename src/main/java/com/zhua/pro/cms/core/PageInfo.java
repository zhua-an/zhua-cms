package com.zhua.pro.cms.core;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PageInfo
 * @Description TODO
 * @Author zhua
 * @Date 2020/7/16 19:15
 * @Version 1.0
 */
public class PageInfo<T> implements Serializable {
    /**
     * 页数
     */
    public int page;
    /**
     * 每页数
     */
    public int limit;

    //结果集
    private List<T> list;

    PageInfo() {
        this.page = 1;
        this.limit = 10;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }
}
