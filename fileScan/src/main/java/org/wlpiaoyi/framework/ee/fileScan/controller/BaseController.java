package org.wlpiaoyi.framework.ee.fileScan.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.wlpiaoyi.framework.ee.fileScan.config.FileConfig;
import org.wlpiaoyi.framework.ee.fileScan.domain.model.FileInfo;
import org.wlpiaoyi.framework.ee.fileScan.service.IFileService;

import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Controller
@RestController
@RequestMapping("")
public class BaseController {

    @Autowired
    private FileConfig fileConfig;

    @Autowired
    private IFileService fileService;

    @SneakyThrows
    @RequestMapping("/")
    @Operation(summary = "文件是否存在 请求", description = "文件是否存在")
    @ResponseBody
    public void infoTreeHref(@RequestParam(required = false, defaultValue = "1") Integer deepCount,
                             HttpServletResponse response) {
        FileInfo fileInfo = this.fileService.scanFileInfo(null, deepCount);
        this.fileService.resHtml(fileInfo, response);
    }
}
