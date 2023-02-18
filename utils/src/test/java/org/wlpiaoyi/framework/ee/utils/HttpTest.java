package org.wlpiaoyi.framework.ee.utils;

import lombok.SneakyThrows;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.ValueUtils;
import org.wlpiaoyi.framework.utils.encrypt.aes.Aes;
import org.wlpiaoyi.framework.utils.gson.GsonBuilder;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/15 23:09
 * {@code @version:}:       1.0
 */
public class HttpTest {
    @Before
    public void setUp() throws Exception {}

    @SneakyThrows
    @Test
    public void test() throws IOException {
        String token1 = StringUtils.getUUID32();
        String token2 = StringUtils.getUUID32();
        this.test1(token1);
        Thread.sleep(1001);
        this.test1(token2);
        Thread.sleep(501);
        this.test1(token1);
        Thread.sleep(3001);
        this.test1(token1);
    }
    @SneakyThrows
    public void test1(String token) throws IOException {

        Aes aes = Aes.create().setKey("abcd567890ABCDEF1234567890ABCDEF").setIV("abcd567890123456").load();
        String bodyArg = GsonBuilder.gsonDefault().toJson(new HashMap(){{
            put("a","1");
            put("b","2");
            put("c","3");
            put("d","4");
        }}, Map.class);
        Response<byte[]> response = HttpClient.instance(
                        Request.initJson("http://127.0.0.1:8081/test/kk/testGet2")
                                .setHeader("token", token)
                                .setParam("v1", "123")
                                .setMethod(Request.Method.Post).setHeader(
                                        "Content-Type","application/json;UTF-8"
                                ).setProxy("127.0.0.1", 8888)
                                .setBody(aes.encrypt(bodyArg.getBytes(StandardCharsets.UTF_8)))
                )
                .setRpClazz(byte[].class)
                .response();
        byte[] arg = response.getBody();
        if(ValueUtils.isBlank(arg))
            return;
        System.out.println(arg);

        System.out.println(new String(aes.decrypt(arg)));
    }

    @After
    public void tearDown() throws Exception {

    }
}
