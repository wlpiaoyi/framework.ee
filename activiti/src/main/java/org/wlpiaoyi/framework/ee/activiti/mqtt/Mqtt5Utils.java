package org.wlpiaoyi.framework.ee.activiti.mqtt;

/**
 * <p><b>{@code @author:}</b>wlpiaoyi</p>
 * <p><b>{@code @description:}</b></p>
 * <p><b>{@code @date:}</b>2025-08-27 14:48:28</p>
 * <p><b>{@code @version:}:</b>1.0</p>
 */
public class Mqtt5Utils {

    /**
     * <p><b>{@code @description:}</b>
     * 获取订阅主题
     * </p>
     *
     * <p><b>@param</b> <b>superTemplateId</b>
     * {@link String}
     * 超级模板Id
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * 超级设备Id
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/27 14:29</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String GetSubscribeTopic(String superTemplateId, String superDeviceId){
        return "/" + superTemplateId + "/" + superDeviceId + "/#";
    }

    /**
     * <p><b>{@code @description:}</b>
     * 获取推送属性值主题T
     * </p>
     *
     * <p><b>@param</b> <b>superTemplateId</b>
     * {@link String}
     * 超级模板Id
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * 超级设备Id
     * </p>
     *
     * <p><b>@param</b> <b>abilityCode</b>
     * {@link String}
     * 能力值
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/27 14:37</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String GetPublishPropertiesReportTopic(String superTemplateId, String superDeviceId, String abilityCode){
        return "/" + superTemplateId + "/" + superDeviceId + "/" + abilityCode + "/properties/report";
    }

    public static String GetPublishEventReportTopic(String superTemplateId, String superDeviceId, String abilityCode){
        return "/" + superTemplateId + "/" + superDeviceId + "/" + abilityCode + "/event";
    }

    /**
     * <p><b>{@code @description:}</b>
     * 属性上报内容构建器
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>abilityCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>deviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>nodeCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>properties</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/27 14:47</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String BuildPropertiesReportContent(String superDeviceId, String abilityCode, String deviceId, String nodeCode, String properties) {
        return """
        {
            "headers": {
                "version": "3.0"
            },
            "deviceId": "%s",
            "abilities": [
                {
                    "uri": {
                        "abilityCode": "%s",
                        "nodes": [
                            {
                                "deviceId": "%s",
                                "instanceName": "",
                                "nodeCode": "%s"
                            }
                        ]
                    },
                    properties:%s
                }
            ]
        }
        """.formatted(superDeviceId, abilityCode, deviceId, nodeCode, properties);
    }

    /**
     * <p><b>{@code @description:}</b>
     * 事件上报内容构建器
     * </p>
     *
     * <p><b>@param</b> <b>superDeviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>abilityCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>deviceId</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>nodeCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>eventCode</b>
     * {@link String}
     * </p>
     *
     * <p><b>@param</b> <b>eventData</b>
     * {@link String}
     * </p>
     *
     * <p><b>{@code @date:}</b>2025/8/27 15:16</p>
     * <p><b>{@code @return:}</b>{@link String}</p>
     * <p><b>{@code @author:}</b>wlpiaoyi</p>
     */
    public static String BuildEventReportContent(String superDeviceId, String abilityCode, String deviceId, String nodeCode, String eventCode, String eventData) {
        return """
        {
            "headers": {
                "version": "3.0"
            },
            "deviceId": "%s",
            "abilities": [
                {
                    "uri": {
                        "abilityCode": "%s",
                        "nodes": [
                            {
                                "deviceId": "%s",
                                "instanceName": "",
                                "nodeCode": "%s"
                            }
                        ]
                    },
                    "event":"%s",
                    "data": %s
                }
            ]
        }
        """.formatted(superDeviceId, abilityCode, deviceId, nodeCode, eventCode, eventData);
    }
}
