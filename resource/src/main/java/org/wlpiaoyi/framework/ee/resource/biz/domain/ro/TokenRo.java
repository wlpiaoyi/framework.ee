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
 * {@code @description:} 	令牌 请求包装类
 * {@code @date:} 			2025-11-20 14:57:00
 * {@code @version:}: 		1.0
 */
public class TokenRo {
    @Data
    @Schema(description = "令牌 请求实例")
	public static class Query extends org.wlpiaoyi.framework.ee.utils.request.Query implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** 令牌 **/
		@Schema(name = "token" , description = "令牌")
		@NotBlank(message = "令牌不能为空")
		private String token;
		/** 公钥 **/
		@Schema(name = "publicKey" , description = "公钥")
		@NotBlank(message = "公钥不能为空")
		private String publicKey;
		/** 私钥 **/
		@Schema(name = "privateKey" , description = "私钥")
		@NotBlank(message = "私钥不能为空")
		private String privateKey;
    }

    @Data
    @Schema(description = "令牌 请求实例")
    public static class Submit implements Serializable {

        private static final long serialVersionUID = 1L;

		@JsonSerialize(using = ToStringSerializer.class)
		@Schema(description = "主键id")
		@TableId(value = "id", type = IdType.ASSIGN_ID)
		private Long id;
		/** 令牌 **/
		@Schema(name = "token" , description = "令牌")
		@NotBlank(message = "令牌不能为空")
		private String token;
		/** 公钥 **/
		@Schema(name = "publicKey" , description = "公钥")
		@NotBlank(message = "公钥不能为空")
		private String publicKey;
		/** 私钥 **/
		@Schema(name = "privateKey" , description = "私钥")
		@NotBlank(message = "私钥不能为空")
		private String privateKey;
    }
}
