package org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	图片信息 视图实体类
 * {@code @date:} 			2023-12-28 16:38:04
 * {@code @version:}: 		1.0
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ImageInfoVo extends ImageInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	/** 缩略图信息 **/
	@Schema(description = "缩略图信息")
	private Object thumbnailInfo;

}
