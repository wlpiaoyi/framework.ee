package org.wlpiaoyi.framework.ee.fileScan.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.wlpiaoyi.framework.ee.fileScan.config.FileConfig;
import org.wlpiaoyi.framework.ee.fileScan.domain.model.FileInfo;
import org.wlpiaoyi.framework.ee.fileScan.service.IFileService;
import org.wlpiaoyi.framework.ee.utils.response.R;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import javax.annotation.security.PermitAll;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

@Slf4j
@Controller
@RestController
@RequestMapping("/file")
@Tag(name = "文件控制器")
public class FileController {

    @Autowired
    private FileConfig fileConfig;

    @Autowired
    private IFileService fileService;


    @SneakyThrows
    @RequestMapping("/info-tree")
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public R<FileInfo> infoTree(@RequestParam(required = false, defaultValue = "1") Integer deepCount) {
        String basePath = "";
        return R.success(this.fileService.scanFileInfo(null, deepCount));
    }
    @SneakyThrows
    @RequestMapping("/info-tree-href")
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public void infoTreeHref(@RequestParam(required = false, defaultValue = "1") Integer deepCount,
                                    HttpServletResponse response) {
        FileInfo fileInfo = this.fileService.scanFileInfo(null, deepCount);
        this.fileService.resHtml(fileInfo, response);
    }


    @SneakyThrows
    @RequestMapping("/info-tree/{fingerprint}")
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public R<FileInfo> infoTree(@Validated @Parameter(description = "fingerprint", required = false) @PathVariable String fingerprint,
                                @RequestParam(required = false, defaultValue = "1") Integer deepCount) {
        String basePath = "";
        if(ValueUtils.isNotBlank(fingerprint)){
            basePath = new String(this.fileConfig.getAesCipher().decrypt(this.fileConfig.dataDecode(fingerprint)));
        }
        return R.success(this.fileService.scanFileInfo(new File(this.fileConfig.getFileMenu() + basePath), deepCount));
    }


    @SneakyThrows
    @RequestMapping("/info-tree-href/{fingerprint}")
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public void infoTreeHref(@Validated @Parameter(description = "fingerprint", required = false) @PathVariable String fingerprint,
                             @RequestParam(required = false, defaultValue = "1") Integer deepCount,
                             HttpServletResponse response) {
        String basePath = "";
        if(ValueUtils.isNotBlank(fingerprint)){
            basePath = new String(this.fileConfig.getAesCipher().decrypt(this.fileConfig.dataDecode(fingerprint)));
        }
        FileInfo fileInfo = this.fileService.scanFileInfo(new File(this.fileConfig.getFileMenu() + basePath), deepCount);

        this.fileService.resHtml(fileInfo, response);
    }


    @SneakyThrows
    @RequestMapping("/download/{base64Md5FingerprintStr}/{authKey}")
    @Operation(summary = "下载单个文件 请求", description = "加载文件")
    @ResponseBody
    @PermitAll
    public void download(@Validated @Parameter(description = "base64Md5FingerprintStr") @PathVariable String base64Md5FingerprintStr,
                         @Validated @Parameter(description = "authKey") @PathVariable String authKey,
                         @RequestParam(required = false, defaultValue = "inline") String readType,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        this.fileService.download(this.fileService.getFingerprint(base64Md5FingerprintStr), new HashMap(){{
            put("readType", readType);
        }},request, response);
    }

    @SneakyThrows
    @RequestMapping("/download/{base64Md5FingerprintStr}/{authKey}/{fileName}")
    @Operation(summary = "下载单个文件 请求", description = "加载文件")
    @ResponseBody
    @PermitAll
    public void download(@Validated @Parameter(description = "base64Md5FingerprintStr") @PathVariable String base64Md5FingerprintStr,
                         @Validated @Parameter(description = "authKey") @PathVariable String authKey,
                         @Validated @Parameter(description = "fileName") @PathVariable String fileName,
                         @RequestParam(required = false, defaultValue = "inline") String readType,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        this.fileService.download(this.fileService.getFingerprint(base64Md5FingerprintStr), new HashMap(){{
            put("readType", readType);
        }},request, response);
    }

}
