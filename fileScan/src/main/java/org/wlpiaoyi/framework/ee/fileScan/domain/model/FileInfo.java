package org.wlpiaoyi.framework.ee.fileScan.domain.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.wlpiaoyi.framework.utils.ValueUtils;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.lang.ref.WeakReference;
import java.util.Collection;

/**
 * <p><b>{@code @author:}</b>         wlpiaoyi</p>
 * <p><b>{@code @description:}</b>    文件信息</p>
 * <p><b>{@code @date:}</b>           2024/3/11 22:32</p>
 * <p><b>{@code @version:}</b>       1.0</p>
 */

@Data
public class FileInfo {

    /** 是否是文件夹 **/
    @Schema(description = "是否是文件夹")
    private boolean isDict = false;

    private boolean isRoot = false;

    /** 名称 **/
    @Schema(description = "名称")
    @NotNull(message = "文件名称不能为空")
    private String name;

    /** 文件后缀 **/
    @Schema(description = "文件后缀")
    private String suffix;

    /** 上级目录 **/
    @Schema(description = "上级目录")
    private WeakReference<FileInfo> parent;

    /** 深度 **/
    @Schema(description = "深度")
    private int deep = 0;

    /** 叶子节点 **/
    @Schema(description = "叶子节点")
    private boolean isLeaf = false;

    /** 下级集合 **/
    @Schema(description = "下级集合")
    private Collection<FileInfo> children;

    /** 文件路径 **/
    @Schema(description = "文件路径")
    private String path;

    public void setPath(String path) {
        if(ValueUtils.isNotBlank(path)){
            path = path.replaceAll("\\\\", "/");
        }
        this.path = path;
    }

    /** 文件路径流 **/
    @Schema(description = "文件路径流")
    @NotBlank(message = "文件路径流不能为空")
    private String pathBuffer;


    public int hashCode(){
        String str = this.toString();
        if(ValueUtils.isBlank(str)){
            return super.hashCode();
        }
        return str.hashCode();
    }

    public String toString(){
        if(ValueUtils.isBlank(this.getName())){
            return "";
        }
        if(this.getParent() == null || this.getParent().get() == null){
            return this.getName().replaceAll("\\\\", "/");
        }
        return this.getPath().replaceAll("\\\\", "/");
    }

}
