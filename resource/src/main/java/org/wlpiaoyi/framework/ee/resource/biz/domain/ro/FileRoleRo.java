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
 * {@code @description:} 	文件权限 请求包装类
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
public class FileRoleRo {
    @Data
    @Schema(description = "文件权限 请求实例")
	public static class Query extends org.wlpiaoyi.framework.ee.utils.request.Query implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** name **/
		@Schema(name = "name" , description = "name")
		@NotBlank(message = "name不能为空")
		private String name;
		/** 对外数据权限:二进制index（0:查看, 1:下载, 2:修改/删除） **/
		@Schema(name = "permissionValue" , description = "对外数据权限:二进制index（0:查看: 1:下载, 2:修改/删除）")
		@NotNull(message = "对外数据权限:二进制index（0:查看不能为空")
		private Integer permissionValue;
		/** 数据权限类型:0 默认, 1  向下继承,  2 被动向下继承 **/
		@Schema(name = "permissionType" , description = "数据权限类型:0 默认: 1  向下继承,  2 被动向下继承")
		@NotNull(message = "数据权限类型:0 默认不能为空")
		private Byte permissionType;
    }

    @Data
    @Schema(description = "文件权限 请求实例")
    public static class Submit implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** name **/
		@Schema(name = "name" , description = "name")
		@NotBlank(message = "name不能为空")
		private String name;
		/** 对外数据权限:二进制index（0:查看, 1:下载, 2:修改/删除） **/
		@Schema(name = "permissionValue" , description = "对外数据权限:二进制index（0:查看: 1:下载, 2:修改/删除）")
		@NotNull(message = "对外数据权限:二进制index（0:查看不能为空")
		private Integer permissionValue;
		/** 数据权限类型:0 默认, 1  向下继承,  2 被动向下继承 **/
		@Schema(name = "permissionType" , description = "数据权限类型:0 默认: 1  向下继承,  2 被动向下继承")
		@NotNull(message = "数据权限类型:0 默认不能为空")
		private Byte permissionType;
    }
}
