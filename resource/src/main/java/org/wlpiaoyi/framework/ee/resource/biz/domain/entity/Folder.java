package org.wlpiaoyi.framework.ee.resource.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.wlpiaoyi.framework.ee.resource.domain.entity.BaseEntity;
import lombok.experimental.Accessors;
import java.io.Serializable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;


/**
 * <p><b>{@code @author:}</b>         admin:DESKTOP-RLUL55B</p>
 * <p><b>{@code @description:}</b>
 * <div style='padding: 5px; margin-left: 5px; margin-bottom: 5px;'>
 * 文件夹 实体类
 * </div>
 * </p>
 * <p><b>{@code @date:}</b>           2025-12-22 17:09:27</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 * <hr/>
 */
@Data
@Accessors(chain=true)
@TableName("res_folder")
@Schema(description = "文件夹")
@EqualsAndHashCode(callSuper = true)
public class Folder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


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