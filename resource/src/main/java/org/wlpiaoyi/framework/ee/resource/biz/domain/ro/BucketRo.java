package org.wlpiaoyi.framework.ee.resource.biz.domain.ro;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;


/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	桶 请求包装类
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
public class BucketRo {
    @Data
    @Schema(description = "桶 请求实例")
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
		/** 所属令牌,如果为空就是公共桶 **/
		@Schema(name = "tokenOwnerId" , description = "所属令牌:如果为空就是公共桶")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long tokenOwnerId;
    }

    @Data
    @Schema(description = "桶 请求实例")
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
		/** 所属令牌,如果为空就是公共桶 **/
		@Schema(name = "tokenOwnerId" , description = "所属令牌:如果为空就是公共桶")
		@JsonSerialize(using = ToStringSerializer.class)
		private Long tokenOwnerId;
    }
}
