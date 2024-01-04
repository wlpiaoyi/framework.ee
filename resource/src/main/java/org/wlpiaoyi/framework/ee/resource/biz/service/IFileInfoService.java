package org.wlpiaoyi.framework.ee.resource.biz.service;

import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.domain.vo.FileInfoVo;
import org.wlpiaoyi.framework.ee.resource.service.IBaseService;

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
        void afterSave(boolean saveRes, Map funcMap, FileInfo entity);
    }

    interface FileInfoUpdateInterceptor{
        void afterUpdate(boolean updateRes, Map funcMap, FileInfo entity);
    }



    /**
     * 获取详情
     * @param id 文件Id
     * @return: vo.domain.biz.org.wlpiaoyi.framework.ee.resource.FileInfoVo
     * @author: wlpia
     * @date: 2024/1/3 12:31
     */
    FileInfoVo detail(Long id);

    /**
     * 获取缩略图对应的文件
     * @param fileInfo
     * @return: entity.domain.biz.org.wlpiaoyi.framework.ee.resource.FileInfo
     * @author: wlpia
     * @date: 2023/12/30 19:39
     */
    FileInfo getThumbnailFileByFileInfo(FileInfo fileInfo);

    /**
     * 清理文件
     * @return
     */
    List<String> cleanFile();


    boolean save(FileInfo entity, Map funcMap, FileInfoSaveInterceptor interceptor);

    boolean updateById(FileInfo entity, Map funcMap, FileInfoUpdateInterceptor interceptor);

}
