package org.wlpiaoyi.framework.ee.resource.biz.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.domain.entity.BaseEntity;

import javax.validation.constraints.NotBlank;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件信息 视图实体类
 * {@code @date:} 			2023-12-08 17:30:35
 * {@code @version:}: 		1.0
 */
@lombok.Data
@EqualsAndHashCode(callSuper = true)
public class FileInfoVo extends FileInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 文件识别标记 **/
	@Schema(description = "文件识别标记")
	@NotBlank(message = "token不能为空")
	private String token;

	/** 拓展信息 **/
	@Schema(description = "拓展信息")
	private BaseEntity expandInfo;

	@Override
	public void cleanKeyData() {
		super.cleanKeyData();
		if(this.expandInfo != null){
			this.expandInfo.cleanKeyData();
		}
	}
}
