package org.wlpiaoyi.framework.ee.utils.advice;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

/**
 * 配置信息
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 20:47
 * {@code @version:}:       1.0
 */
@Data
@Component
public class ConfigModel {

    @Value("${wlpiaoyi.ee.cors.data.json.patterns}")
    private String jsonPatterns;
    @Value("${wlpiaoyi.ee.cors.data.security.patterns}")
    private String securityPatterns;
    @Value("${wlpiaoyi.ee.cors.data.idempotence.patterns}")
    private String idempotencePatterns;
    @Value("${wlpiaoyi.ee.cors.data.idempotence.duriTime}")
    private Integer idempotenceDuriTime;
    @Value("${wlpiaoyi.ee.cors.data.charset_name}")
    private String charsetName = "UTF-8";

    public boolean checkJsonParse(String path){
        return Pattern.matches(this.getJsonPatterns(), path);
    }

    public boolean checkSecurityParse(String path){
        return Pattern.matches(this.getSecurityPatterns(), path);
    }

    public boolean checkIdempotencePatterns(String path){
        return Pattern.matches(this.getIdempotencePatterns(), path);
    }
}
