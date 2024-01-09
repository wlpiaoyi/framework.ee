package org.wlpiaoyi.framework.ee.resource.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.wlpiaoyi.framework.ee.resource.domain.entity.BaseEntity;
import lombok.experimental.Accessors;
import java.io.Serializable;
import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import javax.validation.constraints.NotBlank;



/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	视频信息 实体类
 * {@code @date:} 			2024-01-08 14:07:23
 * {@code @version:}: 		1.0
 */
@Data
@Accessors(chain=true)
@TableName("biz_video_info")
@Schema(description = "视频信息")
@EqualsAndHashCode(callSuper = true)
public class VideoInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 视频宽度 **/
    @Schema(description = "视频宽度")
    private Integer width;

    /** 视频高度 **/
    @Schema(description = "视频高度")
    private Integer height;

    /** 文件id **/
    @Schema(description = "文件id")
    @NotNull(message = "文件id不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long fileId;

    /** 文件后缀 **/
    @Schema(description = "文件后缀")
    @NotBlank(message = "文件后缀不能为空")
    private String suffix;

    /** 视频时长(ms) **/
    @Schema(description = "视频时长(ms)")
    @NotNull(message = "视频时长(ms)不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long duration;

    /** 视频截图Id **/
    @Schema(description = "视频截图Id")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long screenshotId;

}
