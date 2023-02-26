package org.wlpiaoyi.framework.ee.utils.filter;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.utils.ValueUtils;

import java.util.regex.Pattern;

/**
 * 配置信息
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 20:47
 * {@code @version:}:       1.0
 */
@Getter
@Component
public class ConfigModel {

//    @Value("${wlpiaoyi.ee.cors.data.gson.patterns}")
//    private String[] gsonPatterns;
    @Value("${wlpiaoyi.ee.cors.data.security.patterns}")
    private String[] securityPatterns;
    @Value("${wlpiaoyi.ee.cors.data.idempotence.patterns}")
    private String[] idempotencePatterns;
    @Value("${wlpiaoyi.ee.cors.data.idempotence.duriTime}")
    private Integer idempotenceDuriTime;
    @Value("${wlpiaoyi.ee.cors.data.idempotence.sectionTime}")
    private Integer idempotenceSectionTime;
    @Value("${wlpiaoyi.ee.cors.data.snowflake.workerId}")
    private Byte workerId;
    @Value("${wlpiaoyi.ee.cors.data.snowflake.datacenterId}")
    private Byte datacenterId;
    @Value("${wlpiaoyi.ee.cors.data.charset_name}")
    private String charsetName = "UTF-8";

//    public final boolean checkGsonParse(String path){
//        return CheckPatterns(path, this.getGsonPatterns());
//    }

    public final boolean checkSecurityParse(String path){
        return CheckPatterns(path, this.getSecurityPatterns());
    }

    public final boolean checkIdempotencePatterns(String path){
        return CheckPatterns(path, this.getIdempotencePatterns());
    }


    protected static boolean CheckPatterns(String path, String[] patterns){
        if(ValueUtils.isBlank(patterns)){
            return false;
        }
        for (String pattern : patterns) {
            if(!Pattern.matches(pattern, path)){
                return false;
            }
        }
        return true;
    }

}
