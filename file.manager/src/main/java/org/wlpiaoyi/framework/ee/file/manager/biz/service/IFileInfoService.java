package org.wlpiaoyi.framework.ee.file.manager.biz.service;

import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.impl.FileInfoServiceImpl;
import org.wlpiaoyi.framework.ee.file.manager.service.IBaseService;

import java.util.List;


/**
 * {@code @author:} 		wlpia:WLPIAOYI-DELL
 * {@code @description:} 	文件信息 服务类接口
 * {@code @date:} 			2023-12-08 16:48:27
 * {@code @version:}: 		1.0
 */
public interface IFileInfoService extends IBaseService<FileInfo> {

    interface FileInfoSaveInterceptor{
        void afterSave(boolean saveRes, Object userInfo, FileInfo entity);
    }

    interface FileInfoUpdateInterceptor{
        void afterUpdate(boolean updateRes, Object userInfo, FileInfo entity);
    }

    /**
     * 清理文件
     * @return
     */
    List<String> cleanFile();


    boolean save(FileInfo entity, Object userInfo, FileInfoSaveInterceptor interceptor);

    boolean updateById(FileInfo entity, Object userInfo, FileInfoUpdateInterceptor interceptor);

}
