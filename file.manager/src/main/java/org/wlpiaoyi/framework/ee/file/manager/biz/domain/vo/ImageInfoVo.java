package org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo;

import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;
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

}