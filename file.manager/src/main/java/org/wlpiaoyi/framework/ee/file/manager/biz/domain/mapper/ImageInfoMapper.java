package org.wlpiaoyi.framework.ee.file.manager.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.ro.ImageInfoRo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.ImageInfoVo;

import java.util.Collection;
import java.util.List;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	图片信息 Mapper 接口
 * {@code @date:} 			2023-12-28 16:38:04
 * {@code @version:}: 		1.0
 */
public interface ImageInfoMapper extends BaseMapper<ImageInfo> {

    /**
     * 根据图片查询出缩略图
     * @param ids
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2023/12/30 17:59
     */
    List<Long> selectThumbnailIdByIds(@Param("ids") Collection<Long> ids);

    /**
     * 查询出已经删除的图片
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2023/12/30 18:27
     */
    List<Long> selectDeletedIds();

    /**
     * 查询出已经删除的图片对应的文件Id
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2023/12/30 18:28
     */
    List<Long> selectDeletedFileIds();

    /**
     * 删除图片
     * @param
     * @return: int
     * @author: wlpia
     * @date: 2023/12/30 18:44
     */
    int deletedImages();
}
