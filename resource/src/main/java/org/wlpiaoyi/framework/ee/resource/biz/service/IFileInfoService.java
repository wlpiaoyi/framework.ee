package org.wlpiaoyi.framework.ee.resource.biz.service;

import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.ImageInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FileInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.ImageInfoVo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.VideoInfoVo;
import org.wlpiaoyi.framework.ee.resource.service.IBaseService;

import java.io.InputStream;
import java.util.List;
import java.util.Map;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件信息 服务类接口
 * {@code @date:} 			2023-12-08 16:48:27
 * {@code @version:}: 		1.0
 */
public interface IFileInfoService extends IBaseService<FileInfo> {

    interface FileInfoSaveInterceptor{
        void beforeSave(Map funcMap, FileInfo entity);
        void afterSave(Map funcMap, FileInfo entity);
    }

    interface FileInfoUpdateInterceptor{
        void beforeUpdate(Map funcMap, FileInfo entity);
        void afterUpdate(Map funcMap, FileInfo entity);
    }

    /**
     * 详情
     * @param id
     * @return: org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FileInfoVo
     * @author: wlpia
     * @date: 2024/1/11 16:47
     */
    FileInfoVo detail(Long id);

    /**
     * 存储文件
     * @param fileIo 文件IO
     * @param entity 文件实体信息
     * @param funcMap 拓展字段
     * @param interceptor 存储回调
     * @return: java.lang.String
     * @author: wlpia
     * @date: 2024/1/8 17:26
     */
    String save(Object fileIo, FileInfo entity, Map funcMap, FileInfoSaveInterceptor interceptor);


    /**
     * 删除单个资源文件
     * @param filePath 文字路径
     * @return: void
     * @author: wlpia
     * @date: 2024/1/9 10:25
     */
    boolean deleteFile(String filePath);

    /**
     * 清理文件
     * @return
     */
    List<String> cleanFile();

}
