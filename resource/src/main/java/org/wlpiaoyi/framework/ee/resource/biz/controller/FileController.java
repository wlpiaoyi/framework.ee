package org.wlpiaoyi.framework.ee.resource.biz.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.resource.biz.domain.entity.FileInfo;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileInfoService;
import org.wlpiaoyi.framework.ee.resource.biz.service.IFileService;
import org.wlpiaoyi.framework.ee.resource.config.FileConfig;
import org.wlpiaoyi.framework.ee.utils.response.R;
import org.wlpiaoyi.framework.utils.ValueUtils;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/9/16 15:38
 * {@code @version:}:       1.0
 */
@Slf4j
@Controller
@RestController
@RequestMapping("/file")
@Tag(name = "文件上传")
public class FileController {

    @Autowired
    private IFileService fileService;

    @Autowired
    private IFileInfoService fileDataService;


    @Autowired
    private FileConfig fileConfig;

    @SneakyThrows
    @GetMapping("/exists")
    @ApiOperationSupport(order = 1)
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public R<Boolean> exists(@Validated @Parameter(description = "文件指纹, MD5(path)+SHA(name)") @RequestParam(value = "fingerprint") String fingerprint,
                             HttpServletResponse response) {
        boolean existFile = false;
        File filePath = new File(this.fileConfig.getFilePathByFingerprint(fingerprint));
        if(filePath.exists() && filePath.isFile()){
            existFile = true;
        }
        return R.success(existFile);
    }

    @SneakyThrows
    @PostMapping("/upload")
    @ApiOperationSupport(order = 1)
    @Operation(summary = "上传单个文件 请求", description = "上传单个文件")
    @ResponseBody
    public R<FileInfo> upload(@Validated  @Parameter(description = "上传的文件") @RequestParam(value = "file") MultipartFile file,
//                              @Parameter(description = "文件指纹, MD5(path)+SHA(name)") @RequestParam(value = "fingerprint", required = false) String fingerprint,
                              @Parameter(description = "是否需要签名验证") @RequestParam(value = "isVerifySign", required = false, defaultValue = "0") byte isVerifySign,
                              @Parameter(description = "文件缩略图比例,图片专用0.0~1.0") @RequestParam(value = "thumbnailSize", required = false, defaultValue = "-1") double thumbnailSize,
                              @Parameter(description = "文件名称") @RequestParam(value = "name", required = false) String name,
                              @Parameter(description = "文件格式") @RequestParam(value = "suffix", required = false) String suffix,
                              HttpServletResponse response) {
        FileInfo fileInfo = new FileInfo();
        fileInfo.setIsVerifySign(isVerifySign);
        if(ValueUtils.isNotBlank(name)){
            fileInfo.setName(name);
        }
        if(ValueUtils.isNotBlank(suffix)){
            fileInfo.setSuffix(suffix);
        }
        if(ValueUtils.isBlank(fileInfo.getName())){
            fileInfo.setName(file.getOriginalFilename());
        }
        if(ValueUtils.isBlank(fileInfo.getSuffix())){
            if(fileInfo.getName().contains(".")){
                fileInfo.setSuffix(fileInfo.getName().substring(fileInfo.getName().lastIndexOf(".") + 1));
            }else if(file.getOriginalFilename().contains(".")){
                fileInfo.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
            }
        }
        Map funcMap = new HashMap<>();
        funcMap.put("thumbnailSize", thumbnailSize);

        String fileSign = this.fileService.save(file.getInputStream(), fileInfo, funcMap);
        if(ValueUtils.isNotBlank(fileSign)){
            response.setHeader("file-sign", fileSign);
        }
        return R.success(fileInfo);
    }

    @SneakyThrows
    @GetMapping("/download/{token}/{fingerprint}")
    @ApiOperationSupport(order = 2)
    @Operation(summary = "下载单个文件 请求", description = "加载文件")
    @ResponseBody
    @PermitAll
    public void download(@Validated @Parameter(description = "token") @PathVariable String token,
                         @Validated @Parameter(description = "文件指纹") @PathVariable String fingerprint,
                         @Parameter(description = "文件读取类型: attachment,inline") @RequestParam(required = false, defaultValue = "attachment") String readType,
                         @Parameter(description = "数据类型: org,thumbnail") @RequestParam(required = false, defaultValue = "org") String dataType,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        this.fileService.download(token, fingerprint, new HashMap(){{
            put("readType", readType);
            put("dataType", dataType);
        }}, request, response);
    }
}
