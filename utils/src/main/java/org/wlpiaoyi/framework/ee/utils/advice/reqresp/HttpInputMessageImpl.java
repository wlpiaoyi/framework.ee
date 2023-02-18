package org.wlpiaoyi.framework.ee.utils.advice.reqresp;

import com.google.gson.Gson;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpInputMessage;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 22:03
 * {@code @version:}:       1.0
 */
@Slf4j
class HttpInputMessageImpl implements HttpInputMessage {

    private HttpHeaders headers;
    private InputStream body;

    @Setter
    private boolean isCheckJsonParse;
//    @Setter
//    private boolean isCheckSecurityParse;
    @Setter
    private String charsetName = "UTF-8";

    HttpInputMessageImpl() {;

    }

    public void done(HttpInputMessage inputMessage,
                     MethodParameter methodParameter) throws Exception{
        this.headers = inputMessage.getHeaders();
        this.body = inputMessage.getBody();
//        else{
//            if(this.isCheckSecurityParse){
//                Aes aes = securityOption.getAes(inputMessage);
//                if(aes == null){
//                    this.body = inputMessage.getBody();
//                    return;
//                }
//                buffer = IOUtils.toByteArray(inputMessage.getBody());
//                buffer = aes.decrypt(buffer);
//            }else{
//                buffer = IOUtils.toByteArray(inputMessage.getBody());
//            }
//        }

        byte[] buffer = null;
        if(this.isCheckJsonParse){
            buffer = IOUtils.toByteArray(inputMessage.getBody());
            String string = new String(buffer, this.charsetName);
            Gson gson = GsonBuilder.gsonDefault();
            Map<String, Object> mapJson = (Map<String, Object>) gson.fromJson(string, Map.class);
            this.body = IOUtils.toInputStream(gson.toJson(mapJson) + "\n", this.charsetName);
        }
        if(buffer != null) {
            this.body = IOUtils.buffer(new ByteArrayInputStream(buffer));
        }
    }


    @Override
    public InputStream getBody(){
        return body;
    }

    @Override
    public HttpHeaders getHeaders() {
        return headers;
    }
}
