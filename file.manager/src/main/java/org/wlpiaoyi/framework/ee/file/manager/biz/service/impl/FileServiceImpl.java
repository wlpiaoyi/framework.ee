package org.wlpiaoyi.framework.ee.file.manager.biz.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.Charsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.entity.FileMenu;
import org.wlpiaoyi.framework.ee.file.manager.biz.domain.vo.FileMenuVo;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileMenuService;
import org.wlpiaoyi.framework.ee.file.manager.biz.service.IFileService;
import org.wlpiaoyi.framework.ee.file.manager.utils.FileUtils;
import org.wlpiaoyi.framework.ee.file.manager.utils.IdUtils;
import org.wlpiaoyi.framework.ee.file.manager.utils.tools.ModelWrapper;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.data.DataUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.exception.BusinessException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/12/8 16:51
 * {@code @version:}:       1.0
 */
@Slf4j
@Primary
@Service
public class FileServiceImpl implements IFileService {
    @Value("${file.manager.tempPath}")
    private String tempPath;

    @Value("${file.manager.dataPath}")
    private String dataPath;


    @Getter
    private Aes aes;
    {
        try {
            aes = Aes.create().setKey("abcd567890ABCDEF1234567890ABCDEF").setIV("abcd567890123456").load();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private IFileMenuService fileMenuService;

    @SneakyThrows
    @Override
    public boolean upload(FileMenu fileMenu, MultipartFile file) throws IOException {
        String fingerprint = FileUtils.moveToFingerprintPath(file, this.tempPath, this.dataPath);
        if(ValueUtils.isBlank(fileMenu.getId())){
            fileMenu.setId(IdUtils.nextId());
        }

        if(ValueUtils.isBlank(fileMenu.getName())){
            fileMenu.setName(file.getOriginalFilename());
        }
        if(ValueUtils.isBlank(fileMenu.getSize()) && file.getOriginalFilename().contains(".")){
            fileMenu.setSuffix(file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1));
        }
        fileMenu.setSize(DataUtils.getSize(this.getFilePath(fingerprint)));
        fileMenu.setFingerprint(fingerprint);
        fileMenu.setToken(ValueUtils.bytesToHex(this.aes.encrypt(fileMenu.getId().toString().getBytes())));
        if(this.fileMenuService.count(Wrappers.<FileMenu>lambdaQuery().eq(FileMenu::getId, fileMenu.getId())) > 0){
            return this.fileMenuService.updateById(fileMenu);
        }else{
            return this.fileMenuService.save(fileMenu);
        }
    }

    public String getFilePath(String fingerprint){
        return this.dataPath + "/" + FileUtils.parseFingerprintToPath(fingerprint);
    }

    private static Map<String, String> contentTypeMap = new HashMap(){{
        put("jpg", "image/jpeg");
        put("jpeg", "image/jpeg");
        put("png", "image/png");
        put("pdf", "application/pdf");
        put("docx", "application/msword");
        put("doc", "application/msword");
        put("default", "application/octet-stream");
    }};
    @Override
    public void download(FileMenu fileMenu, HttpServletResponse response) throws IOException {
        try{
            String ogPath = this.getFilePath(fileMenu.getFingerprint());
            String ft = fileMenu.getSuffix();
            if(ValueUtils.isNotBlank(ft)){
                ft = ft.toLowerCase(Locale.ROOT);
            }
            String contentType = contentTypeMap.get(ft);
            if(ValueUtils.isBlank(contentType)){
                contentType = contentTypeMap.get("default");
            }
            response.setContentType(contentType);
            response.setCharacterEncoding(Charsets.UTF_8.name());
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileMenu.getName(), Charsets.UTF_8.name()));

            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            FileInputStream fis = new FileInputStream(ogPath);
            int nRead;
            byte[] data = new byte[1024];
            while ((nRead = fis.read(data, 0, data.length)) != -1) {
                sos.write(data, 0, nRead);;
                sos.flush();
            }
        }catch (Exception e){
            throw new BusinessException("文件读取异常");
        }
    }

}
