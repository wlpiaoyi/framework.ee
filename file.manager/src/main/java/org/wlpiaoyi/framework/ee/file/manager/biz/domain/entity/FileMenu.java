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
 * {@code @description:} 	文件目录 实体类
 * {@code @date:} 			2023-12-08 17:30:35
 * {@code @version:}: 		1.0
 */
@Data
@Accessors(chain=true)
@TableName("biz_file_menu")
@Schema(description = "文件目录")
@EqualsAndHashCode(callSuper = true)
public class FileMenu extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;


    /** 文件名称 **/
    @Schema(description = "文件名称")
    private String name;

    /** 文件大小 **/
    @Schema(description = "文件大小")
    @NotNull(message = "文件大小不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long size;

    /** token **/
    @Schema(description = "token")
    @NotBlank(message = "token不能为空")
    private String token;


    /** 是否验证签名 0:否 1:是 **/
    @Schema(description = "是否验证签名 0:否 1:是")
    private byte isVerifySign = 0;

    /** 文件指纹 **/
    @Schema(description = "文件指纹")
    @NotBlank(message = "文件指纹不能为空")
    private String fingerprint;

    /** 文件后缀 **/
    @Schema(description = "文件后缀")
    private String suffix;

}
