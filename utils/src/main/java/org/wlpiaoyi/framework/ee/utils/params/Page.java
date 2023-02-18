package org.wlpiaoyi.framework.ee.utils.params;

import lombok.Data;

import java.util.Collection;

/**
 * 
 * @param <T>
 */
@Data
public class Page<T>{

    private Collection<T> datas;
    private int pageIndex = 0;
    private int pageSize = 20;
    private long count = 0;

    public void bundleQuery(Query query){
        this.setPageIndex(query.getPageIndex());
        this.setPageSize(query.getPageSize());
    }

}
