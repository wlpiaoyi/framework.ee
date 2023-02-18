package org.wlpiaoyi.framework.ee.utils;

import lombok.SneakyThrows;
import org.wlpiaoyi.framework.utils.StringUtils;
import org.wlpiaoyi.framework.utils.encrypt.rsa.Rsa;
import org.wlpiaoyi.framework.utils.http.HttpClient;
import org.wlpiaoyi.framework.utils.http.request.Request;
import org.wlpiaoyi.framework.utils.http.response.Response;

/**
 * @Author wlpiaoyi
 * @Date 2022/5/12 16:14
 * @Version 1.0
 */
public class HttpFactory {
    public static final String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC0qrTrNDAqSHhwybcLgkXVOUhcX3RLaOeoOKmZ\n" +
        "aMCuQQnFDKbBNXfwivVYRPEq47EL7ZB4JCN0shHxMtfnmwbTxrPZb3KIPDom1b+k5oo7/qE8mqNw\n" +
        "HDSR6XpqzKJs7Tu8xwob8b16PLFaIDi4tBjOin3P1Lnp4B5d5zoSKCZ9yQIDAQAB";
//    public static final String publicKey =
//            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzr0UqyIwWvFTD5WwP2WJC1bPWcCpZSRE4fPma\n" +
//                    "W9/kbfoBtGGlKe0GWliFndl+Z5/vjAMZ8z+cb/rjh0+S7pefd771vIrz8KtnPGvVvKORu2bT2Upr\n" +
//                    "/+GPDyG8HxKqYMe5qRxWX994oQclYyIWQPlj8C5FNTzccPjaPXR0JcBweQIDAQAB";
    public static final String privateKey =
            "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBALOvRSrIjBa8VMPlbA/ZYkLVs9Zw\n" +
                    "KllJETh8+Zpb3+Rt+gG0YaUp7QZaWIWd2X5nn++MAxnzP5xv+uOHT5Lul593vvW8ivPwq2c8a9W8\n" +
                    "o5G7ZtPZSmv/4Y8PIbwfEqpgx7mpHFZf33ihByVjIhZA+WPwLkU1PNxw+No9dHQlwHB5AgMBAAEC\n" +
                    "gYAy7iscxVtv1lHwdMb8dxFzAD/JOGHIjP1klYfqSMBdbw6+DPLgbdHRSypsNSHAwn6C15zJbjlJ\n" +
                    "jjP+6guUCizPfNxE6/LfG03LhrQ+L21LKy/USBFSEB9sueNgwojSxevoM+pdVFhtRdMxaKzup9kA\n" +
                    "nEq2cR6rT8fs7M6M+UMoAQJBAOBgZBsdK2ULV7M6AWQKcwzz79xypH5l8R+hnNs7PFMeQWgbHPto\n" +
                    "GAHP6hcsArSUApDgF8RgXjEzp2J8keu/F+kCQQDNAmAgG9NgAk8ywxJBggq4OD65g2irzFz69wuP\n" +
                    "HsxxyXfQvDDPjL5bvIi/vxIo2F1UkLsPHAvUyI0x6FBeYsoRAkEAyfVZBaK6xYdxF+xVBiP1rKoA\n" +
                    "sy8pam/9mhgQpK/ru3DXNIp7CruGKFNphBPkF3/F03sxSVvoTGcO+bHgcg6dyQJAFizomvCHl74I\n" +
                    "NRR2uBFJ+Y1T85ssSlELybXJUUzijnhddn20xe6SdLfbDuqrGzH0Pn59TXAaM4USCND5SIxlEQJB\n" +
                    "AL6bAL0IMU48aE9RxX7EbHq+rsvkkbB9JV6f19fbLG/+nwrE+NwegpI+fN0P4LldsQqd37+QFCtt\n" +
                    "p62rKrIvwGw=";

    public static String host = "http://127.0.0.1/friendship";

    public static final boolean headerRandom = true;
    public static final String token = "6fc32c9037e8433587f4d37a7fae12db547d695dcf4140ad919558a428d18725";
    public static final String deviceNo = "yue98adikuwakue";
    public static final int platform = 1;

    public static final Rsa createRsa() throws Exception {
        return Rsa.create().setPublicKey(publicKey).setPrivateKey(privateKey).loadKey();
    }

    @SneakyThrows
    public static final String getUuid1(){
        String uuid = "rsa rduuid-" + System.currentTimeMillis() + ":" + deviceNo + ":" + 1;
        byte[] bytes = createRsa().encryptByPublicKey(uuid.getBytes());
        uuid = StringUtils.base64Encode(bytes).replace("\n", "");
        return uuid;
    }

    @SneakyThrows
    public static final String getUuid2(){
        String uuid = "rsa uuid-" + System.currentTimeMillis() + "";
        byte[] bytes = createRsa().encryptByPublicKey(uuid.getBytes());
        uuid = StringUtils.base64Encode(bytes).replace("\n", "");
        return uuid;
    }

    public static final Request createRequest1(String action){

        return Request.initJson(host + action)
                .setHeader("platform", platform + "")
                .setHeader("random-key", headerRandom + "")
                .setHeader("deviceNo", deviceNo)
                .setHeader("token", token)
                .setHeader("uuid", getUuid1())
                .setHeader("version","1.0.1");
//                .setProxy("127.0.0.1", 8888);
    }

    public static final Request createRequest2(String action){
        return Request.initJson(host + action)
                .setHeader("platform", platform + "")
                .setHeader("random-key", headerRandom + "")
                .setHeader("deviceNo", deviceNo)
                .setHeader("token", token)
                .setHeader("uuid", getUuid2())
                .setHeader("version","1.0.1");
//                .setProxy("127.0.0.1", 8888);
    }

    public static final Response<String> createPostResponse(Request request){
        return HttpClient.instance(request.setMethod(Request.Method.Post))
                .setRpClazz(String.class)
                .response();
    }
    public static final Response<String> createGetResponse(Request request){
        return HttpClient.instance(request.setMethod(Request.Method.Get))
                .setRpClazz(String.class)
                .response();
    }
}
