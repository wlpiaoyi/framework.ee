package org.wlpiaoyi.framework.ee.file.manager.biz.controller;


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
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileMenuService;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileService;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
import org.wlpiaoyi.framework.ee.utils.response.R;
import org.wlpiaoyi.framework.utils.ValueUtils;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.HashMap;

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
    private IFileMenuService fileMenuService;

    @SneakyThrows
    @GetMapping("/exists")
    @ApiOperationSupport(order = 1)
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public R<Boolean> exists(@Validated @Parameter(description = "文件指纹, MD5(path)+SHA(name)") @RequestParam(value = "fingerprint") String fingerprint,
                             HttpServletResponse response) {
        boolean existFile = false;
        File filePath = new File(this.fileService.getFilePathByFingerprint(fingerprint));
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
    public R<FileMenu> upload(@Validated  @Parameter(description = "上传的文件") @RequestParam(value = "file") MultipartFile file,
                              @Parameter(description = "文件指纹, MD5(path)+SHA(name)") @RequestParam(value = "fingerprint", required = false) String fingerprint,
                              @Parameter(description = "是否需要签名验证") @RequestParam(value = "isVerifySign", required = false, defaultValue = "0") byte isVerifySign,
                              @Parameter(description = "文件名称") @RequestParam(value = "name", required = false) String name,
                              @Parameter(description = "文件格式") @RequestParam(value = "suffix", required = false) String suffix,
                              HttpServletResponse response) {
        FileMenu fileMenu = new FileMenu();
        fileMenu.setIsVerifySign(isVerifySign);
        if(ValueUtils.isNotBlank(name)){
            fileMenu.setName(name);
        }
        if(ValueUtils.isNotBlank(suffix)){
            fileMenu.setSuffix(suffix);
        }
        boolean existFile = false;
        if(ValueUtils.isNotBlank(fingerprint)){
            File filePath = new File(this.fileService.getFilePathByFingerprint(fingerprint));
            if(filePath.exists() && filePath.isFile()){
                existFile = true;
            }
        }
        if(existFile){
            this.fileService.synFileMenuByFingerprint(fileMenu, fingerprint);
            this.fileMenuService.save(fileMenu);
        }else{
            this.fileService.upload(fileMenu, file, response);
        }
        return R.success(fileMenu);
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
                         HttpServletRequest request,
                         HttpServletResponse response) {
        this.fileService.download(token, fingerprint, new HashMap(){{
            put("readType", readType);
        }}, request, response);
    }
}
