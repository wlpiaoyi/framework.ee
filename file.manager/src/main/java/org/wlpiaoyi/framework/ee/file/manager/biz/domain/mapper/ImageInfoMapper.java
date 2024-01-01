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
     * 通过已经参数的数据Id查询出图片Id
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2023/12/30 18:28
     */
    List<Long> selectIdsFromDeletedFile();

    /**
     * 根据图片查询出缩略图Id
     * @param ids
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2023/12/30 17:59
     */
    List<Long> selectThumbnailIdByIds(@Param("ids") Collection<Long> ids);

    /**
     * 根据图片Id查询出数据Id
     * @param ids
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2023/12/30 21:37
     */
    List<Long> selectFileIdByIds(@Param("ids") Collection<Long> ids);

    /**
     * 根据图片Id删除图片数据
     * @param ids
     * @return: int
     * @author: wlpia
     * @date: 2023/12/30 18:44
     */
    int deletedByIds(@Param("ids") Collection<Long> ids);
}
