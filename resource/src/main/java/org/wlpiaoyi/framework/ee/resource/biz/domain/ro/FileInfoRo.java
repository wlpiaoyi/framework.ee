package org.wlpiaoyi.framework.ee.resource.biz.domain.ro;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotNull;
import java.io.Serializable;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件信息 请求包装类
 * {@code @date:} 			2023-12-08 17:30:35
 * {@code @version:}: 		1.0
 */
public class FileInfoRo {
    @Data
    @Schema(description = "文件信息 请求实例")
	public static class Query extends org.wlpiaoyi.framework.ee.utils.request.Query implements Serializable {

        private static final long serialVersionUID = 1L;
		/** 文件名称 **/
		@Schema(description = "文件名称")
		private String name;
		/** 文件后缀 **/
		@Schema(description = "文件后缀")
		private String suffix;
    }

    @Data
    @Schema(description = "文件信息 请求实例")
    public static class Submit implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** 文件名称 **/
		@NotNull(message = "文件名称不能为空")
		@Schema(description = "文件名称")
		private String name;
		/** 文件后缀 **/
		@Schema(description = "文件后缀")
		private String suffix;
    }
}
