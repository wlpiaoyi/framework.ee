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

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;
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
    public R<FileInfo> infoTree(@RequestParam(required = false, defaultValue = "1") Integer deepCount,
                                @RequestParam(required = false, defaultValue = "") String fileName,
                                @RequestParam(required = false, defaultValue = "0") Integer fileOrder) {
        String basePath = "";
        return R.success(this.fileService.scanFileInfo(null, deepCount, fileName, fileOrder));
    }
    @SneakyThrows
    @RequestMapping("/info-tree-href")
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public void infoTreeHref(@RequestParam(required = false, defaultValue = "1") Integer deepCount,
                             @RequestParam(required = false, defaultValue = "") String fileName,
                             @RequestParam(required = false, defaultValue = "0") Integer fileOrder,
                             HttpServletResponse response) {
        FileInfo fileInfo = this.fileService.scanFileInfo(null, deepCount, fileName, fileOrder);
        this.fileService.resHtml(fileInfo, response);
    }


    @SneakyThrows
    @RequestMapping("/info-tree/{pathBuffer}")
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public R<FileInfo> infoTree(@Validated @Parameter(description = "pathBuffer", required = false) @PathVariable String pathBuffer,
                                @RequestParam(required = false, defaultValue = "1") Integer deepCount,
                                @RequestParam(required = false, defaultValue = "") String fileName,
                                @RequestParam(required = false, defaultValue = "0") Integer fileOrder) {
        String basePath = "";
        if(ValueUtils.isNotBlank(pathBuffer)){
            basePath = this.fileConfig.getPathByBuffer(pathBuffer);
        }
        return R.success(this.fileService.scanFileInfo(new File(this.fileConfig.absolutePath(basePath)), deepCount, fileName, fileOrder));
    }


    @SneakyThrows
    @RequestMapping("/info-tree-href/{pathBuffer}")
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public void infoTreeHref(@Validated @Parameter(description = "pathBuffer", required = false) @PathVariable String pathBuffer,
                             @RequestParam(required = false, defaultValue = "1") Integer deepCount,
                             @RequestParam(required = false, defaultValue = "") String fileName,
                             @RequestParam(required = false, defaultValue = "0") Integer fileOrder,
                             HttpServletResponse response) {
        String basePath = "";
        if(ValueUtils.isNotBlank(pathBuffer)){
            basePath = this.fileConfig.getPathByBuffer(pathBuffer);
        }
        FileInfo fileInfo = this.fileService.scanFileInfo(new File(this.fileConfig.absolutePath(basePath)), deepCount, fileName, fileOrder);

        this.fileService.resHtml(fileInfo, response);
    }


    @SneakyThrows
    @RequestMapping("/download/{pathBuffer}/{authKey}")
    @Operation(summary = "下载单个文件 请求", description = "加载文件")
    @ResponseBody
    @PermitAll
    public void download(@Validated @Parameter(description = "pathBuffer") @PathVariable String pathBuffer,
                         @Validated @Parameter(description = "authKey") @PathVariable String authKey,
                         @RequestParam(required = false, defaultValue = "inline") String readType,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        this.fileService.download(this.fileConfig.getPathByBuffer(pathBuffer), new HashMap(){{
            put("readType", readType);
        }},request, response);
    }

    @SneakyThrows
    @RequestMapping("/download/{pathBuffer}/{authKey}/{fileName}")
    @Operation(summary = "下载单个文件 请求", description = "加载文件")
    @ResponseBody
    @PermitAll
    public void download(@Validated @Parameter(description = "pathBuffer") @PathVariable String pathBuffer,
                         @Validated @Parameter(description = "authKey") @PathVariable String authKey,
                         @Validated @Parameter(description = "fileName") @PathVariable String fileName,
                         @RequestParam(required = false, defaultValue = "inline") String readType,
                         HttpServletRequest request,
                         HttpServletResponse response) {
        log.info("controller download fingerprint:{}, fileName:{}", pathBuffer, fileName);
        this.fileService.download(this.fileConfig.getPathByBuffer(pathBuffer), new HashMap(){{
            put("readType", readType);
        }},request, response);
    }

}
