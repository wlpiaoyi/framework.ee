package org.wlpiaoyi.framework.ee.file.manager.utils.tools.request;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/16 12:39
 * {@code @version:}:       1.0
 */

import io.swagger.v3.oas.annotations.media.Schema;

import java.io.Serializable;

@Schema(description = "查询条件")
public class Query implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(description = "当前页")
    private Integer pageNum;
    @Schema(description = "每页的数量")
    private Integer pageSize;

    @Schema(description = "增序排序字段")
    private String ascs;

    @Schema(description = "降序排序字段")
    private String descs;


    public Integer getCurrent() {
        return this.pageNum;
    }

    public Integer getPageSize() {
        return this.pageSize;
    }

    public String getAscs() {
        return this.ascs;
    }

    public String getDescs() {
        return this.descs;
    }

    public Query setCurrent(final Integer current) {
        this.pageNum = current;
        return this;
    }

    public Query setPageSize(final Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }
    public Query setAscs(final String ascs) {
        this.ascs = ascs;
        return this;
    }

    public Query setDescs(final String descs) {
        this.descs = descs;
        return this;
    }

}