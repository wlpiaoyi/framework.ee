package org.wlpiaoyi.framework.ee.activiti;

import com.aliyun.dysmsapi20170525.Client;
import com.aliyun.dysmsapi20170525.models.SendSmsRequest;
import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.aliyun.teaopenapi.models.Config;
import com.aliyun.teautil.models.RuntimeOptions;

public class SmsUtil {

    /**
     * 使用AK&SK初始化账号Client
     */
    public static Client createClient(String accessKeyId, String accessKeySecret) throws Exception {
        Config config = new Config()
                .setAccessKeyId(accessKeyId)
                .setAccessKeySecret(accessKeySecret);
        config.endpoint = "dysmsapi.aliyuncs.com";
        return new Client(config);
    }

    /**
     * 发送短信
     */
    public static SendSmsResponse sendSms(String accessKeyId, String accessKeySecret,
                                          String phoneNumbers, String signName,
                                          String templateCode, String templateParam) throws Exception {
        Client client = createClient(accessKeyId, accessKeySecret);

        SendSmsRequest sendSmsRequest = new SendSmsRequest()
                .setPhoneNumbers(phoneNumbers)
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setTemplateParam(templateParam);

        RuntimeOptions runtime = new RuntimeOptions();
        return client.sendSmsWithOptions(sendSmsRequest, runtime);
    }

    /**
     * 示例：发送验证码短信
     */
    public static void sendVerificationCode(String phoneNumber, String code) {
        try {
            String accessKeyId = "LTAI5tRMF8MtH3protGfyn5v";
            String accessKeySecret = "6N26AFClaVqmn4UQehfOwEwgfna4bl";
            String signName = "深圳开鸿数字产业发展";
            String templateCode = "SMS_493410493"; // 模板CODE

            // 模板参数JSON字符串，根据实际模板内容调整
            String templateParam = "{\"title\":\"" + code + "\",\"alarmmst\":\"" + "1alal" + "\"}";

            SendSmsResponse response = sendSms(accessKeyId, accessKeySecret, phoneNumber,
                    signName, templateCode, templateParam);

            System.out.println("短信发送结果:");
            System.out.println("RequestId: " + response.getBody().getRequestId());
            System.out.println("BizId: " + response.getBody().getBizId());
            System.out.println("Code: " + response.getBody().getCode());
            System.out.println("Message: " + response.getBody().getMessage());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) {
        // 发送验证码示例
        String phoneNumber = "18228088049"; // 接收手机号
        String verificationCode = "123456"; // 验证码
        SmsUtil.sendVerificationCode(phoneNumber, verificationCode);
    }
}