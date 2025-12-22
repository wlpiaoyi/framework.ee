package org.wlpiaoyi.framework.ee.resource.biz.domain.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.Bucket;

import java.util.List;

/**
 * {@code @author:} 		admin:DESKTOP-RLUL55B
 * {@code @description:} 	桶 Mapper 接口
 * {@code @date:} 			2025-12-22 17:09:27
 * {@code @version:}: 		1.0
 */
public interface BucketMapper extends BaseMapper<Bucket> {

    /**
     * 单次插入所有字段
     * @param entity
     * @return
     */
    int insertAll(@Param("item") Bucket entity);

	/**
	 * 批量插入所有字段
	 * @param list
	 * @return
	 */
	int insertAllBatch(@Param("items") List<Bucket> list);

	/**
	 * 单次更新所有字段
	 * @param entity
	 * @return
	 */
	int updateAll(@Param("item") Bucket entity);

	/**
	 * 批量更新所有字段
	 * @param list
	 * @return
	 */
	int updateAllBatch(@Param("items") List<Bucket> list);

	/**
	 * 批量物理删除
	 * @param ids
	 * @return
	 */
	int deleteAllBatch(@Param("items") List<Long> ids);

}
