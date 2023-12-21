package org.wlpiaoyi.framework.ee.file.manager.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import org.apache.ibatis.annotations.Param;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.ro.FileMenuRo;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.FileMenuVo;

import java.util.List;

/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件目录 Mapper 接口
 * {@code @date:} 			2023-12-08 17:30:35
 * {@code @version:}: 		1.0
 */
public interface FileMenuMapper extends BaseMapper<FileMenu> {

    /**
     * 查询出已经删除的并且没有关联的文件指纹
     * @return
     */
    List<String> selectDeletedForFingerprint();

    /**
     * 根据文件指纹删除数据
     * @param fingerprints
     * @return
     */
    int deleteByFingerprints(@Param("fingerprints") List<String> fingerprints);

}
