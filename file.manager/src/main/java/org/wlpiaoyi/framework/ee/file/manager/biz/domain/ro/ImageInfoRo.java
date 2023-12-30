package org.wlpiaoyi.framework.ee.file.manager.biz.domain.ro;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.validation.constraints.NotBlank;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	图片信息 请求包装类
 * {@code @date:} 			2023-12-28 16:38:04
 * {@code @version:}: 		1.0
 */
public class ImageInfoRo {
    @Data
    @Schema(description = "图片信息 请求实例")
	public static class Query extends org.wlpiaoyi.framework.ee.utils.request.Query implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** fileId **/
		@Schema(description = "fileId")
		@NotNull(message = "fileId不能为空")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long fileId;
		/** 文件后缀 **/
		@Schema(description = "文件后缀")
		@NotBlank(message = "文件后缀不能为空")
		private String suffix;
		/** width **/
		@Schema(description = "width")
		@NotNull(message = "width不能为空")
		private Integer width;
		/** height **/
		@Schema(description = "height")
		@NotNull(message = "height不能为空")
		private Integer height;
		/** 缩略图 **/
		@Schema(description = "缩略图")
		@NotNull(message = "缩略图不能为空")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long thumbnailId;
    }

    @Data
    @Schema(description = "图片信息 请求实例")
    public static class Submit implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** fileId **/
		@Schema(description = "fileId")
		@NotNull(message = "fileId不能为空")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long fileId;
		/** 文件后缀 **/
		@Schema(description = "文件后缀")
		@NotBlank(message = "文件后缀不能为空")
		private String suffix;
		/** width **/
		@Schema(description = "width")
		@NotNull(message = "width不能为空")
		private Integer width;
		/** height **/
		@Schema(description = "height")
		@NotNull(message = "height不能为空")
		private Integer height;
		/** 缩略图 **/
		@Schema(description = "缩略图")
		@NotNull(message = "缩略图不能为空")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long thumbnailId;
    }
}
