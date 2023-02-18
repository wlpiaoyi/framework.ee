package org.wlpiaoyi.framework.ee.utils.params;

import lombok.Data;

@Data
public class Query<T> {

    private T condition;
    private int pageIndex = 0;
    private int pageSize = 20;

}
