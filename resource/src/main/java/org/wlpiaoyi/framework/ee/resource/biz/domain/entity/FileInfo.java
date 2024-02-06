package org.wlpiaoyi.framework.ee.resource.biz.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
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
 * {@code @description:} 	文件信息 实体类
 * {@code @date:} 			2023-12-08 17:30:35
 * {@code @version:}: 		1.0
 */
@lombok.Data
@Accessors(chain=true)
@TableName("res_file_info")
@Schema(description = "文件信息")
@EqualsAndHashCode(callSuper = true)
public class FileInfo extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 文件名称 **/
    @Schema(description = "文件名称")
    private String name;

    /** 文件大小 **/
    @Schema(description = "文件大小")
    @NotNull(message = "文件大小不能为空")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long size;

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

    @Override
    public void cleanKeyData() {
        super.cleanKeyData();
        this.setFingerprint(null);
    }
}
