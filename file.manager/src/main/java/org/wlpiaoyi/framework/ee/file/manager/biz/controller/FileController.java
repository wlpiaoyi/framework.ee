package org.wlpiaoyi.framework.ee.file.manager.biz.controller;


import com.github.xiaoymin.knife4j.annotations.ApiOperationSupport;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.FileMenuVo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileMenuService;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileService;
import org.wlpiaoyi.framework.ee.file.manager.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.ee.file.manager.utils.tools.response.R;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Rsa;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import javax.annotation.security.PermitAll;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

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
    @PostMapping("/upload")
    @ApiOperationSupport(order = 1)
    @Operation(summary = "上传单个文件 请求", description = "上传单个文件")
    @ResponseBody
    public R<FileMenu> upload(@RequestParam(value = "file") MultipartFile file,
                              @RequestParam(value = "name", required = false) String name) {
        FileMenu fileMenu = new FileMenu();
        if(ValueUtils.isNotBlank(name)){
            fileMenu.setName(name);
            if(fileMenu.getName().contains(".")){
                fileMenu.setSuffix(fileMenu.getName().substring(name.lastIndexOf(".") + 1));
            }
        }
        this.fileService.upload(fileMenu, file);
        return R.success(fileMenu);
    }

    @SneakyThrows
    @GetMapping("/download/{token}/{fingerprint}")
    @ApiOperationSupport(order = 2)
    @Operation(summary = "下载单个文件 请求", description = "加载文件")
    @ResponseBody
    @PermitAll
    public void loading(@PathVariable String token, @PathVariable String fingerprint, HttpServletResponse response) {
        Long id = new Long(new String(this.fileService.getAes().decrypt(ValueUtils.hexToBytes(token))));
        FileMenu fileMenu = this.fileMenuService.getById(id);
        if(fileMenu == null){
            throw new BusinessException("没有找到文件");
        }
        if(!fileMenu.getFingerprint().toUpperCase(Locale.ROOT).equals(fingerprint.toUpperCase(Locale.ROOT))){
            throw new BusinessException("没有找到文件");
        }
        this.fileService.download(fileMenu, response);
    }
}
