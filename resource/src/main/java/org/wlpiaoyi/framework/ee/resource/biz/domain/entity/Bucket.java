package org.wlpiaoyi.framework.ee.resource.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.wlpiaoyi.framework.ee.resource.domain.entity.BaseEntity;
import lombok.experimental.Accessors;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * <p><b>{@code @author:}</b>         admin:DESKTOP-RLUL55B</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 桶 实体类
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025-12-22 17:09:27</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
@Data
@Accessors(chain=true)
@TableName("res_bucket")
@Schema(description = "桶")
@EqualsAndHashCode(callSuper = true)
public class Bucket extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 名称 **/
    @Schema(name = "name" , description = "名称")
    @NotBlank(message = "名称不能为空")
    private String name;

    /** 所属令牌,如果为空就是公共桶 **/
    @Schema(name = "tokenOwnerId" , description = "如果为空就是公共桶")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long tokenOwnerId;

}