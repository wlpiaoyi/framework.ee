package org.wlpiaoyi.framework.ee.resource.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.wlpiaoyi.framework.ee.resource.domain.entity.BaseEntity;
import lombok.experimental.Accessors;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;


/**
 * <p><b>{@code @author:}</b>         admin:DESKTOP-RLUL55B</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 令牌 实体类
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025-12-22 17:09:27</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
@Data
@Accessors(chain=true)
@TableName("res_token")
@Schema(description = "令牌")
@EqualsAndHashCode(callSuper = true)
public class Token extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


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