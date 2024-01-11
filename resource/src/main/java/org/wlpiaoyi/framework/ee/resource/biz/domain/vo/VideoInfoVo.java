package org.wlpiaoyi.framework.ee.resource.biz.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	视频信息 视图实体类
 * {@code @date:} 			2024-01-08 14:07:23
 * {@code @version:}: 		1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class VideoInfoVo extends VideoInfo implements Serializable {
	private static final long serialVersionUID = 1L;

	/** 文件 **/
	@Schema(description = "文件")
	private FileInfo fileInfo;

	/** 视频截图 **/
	@Schema(description = "视频截图")
	private ImageInfo screenshot;

	@Override
	public void cleanKeyData() {
		super.cleanKeyData();
		if(this.fileInfo != null){
			this.fileInfo.cleanKeyData();
		}
		if(this.screenshot != null){
			this.screenshot.cleanKeyData();
		}
	}
}
