package org.wlpiaoyi.framework.ee.resource.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.VideoInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.VideoInfoVo;

import java.util.Collection;
import java.util.List;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	视频信息 Mapper 接口
 * {@code @date:} 			2024-01-08 14:07:23
 * {@code @version:}: 		1.0
 */
public interface VideoInfoMapper extends BaseMapper<VideoInfo> {

    /**
     * 通过已经删除的数据Id查询出视频Id
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2024/1/9 14:05
     */
    List<Long> selectIdsFromDeletedFile();


    /**
     * 根据视频查询出截屏图片Id
     * @param ids 视频Id集合
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2024/1/9 14:23
     */
    List<Long> selectScreenshotImageIdByIds(@Param("ids") List<Long> ids);

    /**
     * 根据视频查询出截屏文件Id
     * @param ids 视频Id集合
     * @return: java.util.List<java.lang.Long>
     * @author: wlpia
     * @date: 2024/1/9 14:23
     */
    List<Long> selectScreenshotFileIdByIds(@Param("ids") List<Long> ids);

    /**
     * 根据Id删除数据
     * @param ids
     * @return: int
     * @author: wlpia
     * @date: 2023/12/30 18:44
     */
    int deletedByIds(@Param("ids") Collection<Long> ids);
}
