package org.wlpiaoyi.framework.ee.resource.biz.domain.ro;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;


/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	文件夹 请求包装类
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
public class FolderRo {
    @Data
    @Schema(description = "文件夹 请求实例")
	public static class Query extends org.wlpiaoyi.framework.ee.utils.request.Query implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** 名称 **/
		@Schema(name = "name" , description = "名称")
		@NotBlank(message = "名称不能为空")
		private String name;
		/** 桶Id **/
		@Schema(name = "bucketId" , description = "桶Id")
		@NotNull(message = "桶Id不能为空")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long bucketId;
		/** 上级Id **/
		@Schema(name = "parentId" , description = "上级Id")
		@NotNull(message = "上级Id不能为空")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long parentId;
		/** 深度 **/
		@Schema(name = "deep" , description = "深度")
		@NotNull(message = "深度不能为空")
		private Integer deep;
		/** 是否是叶子节点 **/
		@Schema(name = "isLeaf" , description = "是否是叶子节点")
		@NotNull(message = "是否是叶子节点不能为空")
		private Byte isLeaf;
    }

    @Data
    @Schema(description = "文件夹 请求实例")
    public static class Submit implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** 名称 **/
		@Schema(name = "name" , description = "名称")
		@NotBlank(message = "名称不能为空")
		private String name;
		/** 桶Id **/
		@Schema(name = "bucketId" , description = "桶Id")
		@NotNull(message = "桶Id不能为空")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long bucketId;
		/** 上级Id **/
		@Schema(name = "parentId" , description = "上级Id")
		@NotNull(message = "上级Id不能为空")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long parentId;
		/** 深度 **/
		@Schema(name = "deep" , description = "深度")
		@NotNull(message = "深度不能为空")
		private Integer deep;
		/** 是否是叶子节点 **/
		@Schema(name = "isLeaf" , description = "是否是叶子节点")
		@NotNull(message = "是否是叶子节点不能为空")
		private Byte isLeaf;
    }
}
