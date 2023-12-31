package org.wlpiaoyi.framework.ee.file.manager.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;

import java.util.List;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件信息 Mapper 接口
 * {@code @date:} 			2023-12-08 17:30:35
 * {@code @version:}: 		1.0
 */
public interface FileInfoMapper extends BaseMapper<FileInfo> {

    /**
     * 查询出已经删除的文件Id
     * @return: java.util.List<java.lang.Long> 文件Id集合
     * @author: wlpia
     * @date: 2023/12/31 21:02
     */
    List<Long> selectDeletedIds();

    /**
     * 查询出已经删除的并且没有关联的文件指纹
     * @param ids 文件Id集合
     * @return: java.util.List<java.lang.String> 文件指纹集合
     * @author: wlpia
     * @date: 2023/12/31 21:03
     */
    List<String> selectCanDeletedFingerprintsByIds(@Param("ids") List<Long> ids);

    /**
     * 根据文件Id删除数据
     * @param ids 文件Id集合
     * @return: int
     * @author: wlpia
     * @date: 2023/12/31 21:05
     */
    int deleteByIds(@Param("ids") List<Long> ids);

}
