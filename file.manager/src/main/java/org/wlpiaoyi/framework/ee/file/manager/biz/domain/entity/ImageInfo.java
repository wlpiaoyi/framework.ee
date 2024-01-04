package org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.wlpiaoyi.framework.ee.file.manager.domain.entity.BaseEntity;
import lombok.experimental.Accessors;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.validation.constraints.NotBlank;



/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	图片信息 实体类
 * {@code @date:} 			2023-12-28 16:38:04
 * {@code @version:}: 		1.0
 */
@Data
@Accessors(chain=true)
@TableName("biz_image_info")
@Schema(description = "图片信息")
@EqualsAndHashCode(callSuper = true)
public class ImageInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


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

    /** 缩略图Id **/
    @Schema(description = "缩略图Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long thumbnailId;

}
