package org.wlpiaoyi.framework.ee.utils.loader.nacos;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/13 11:37
 * {@code @version:}:       1.0
 */
public interface NacosConstant {

    String SENTINEL_ADDR = "127.0.0.1:8858";
    String ZIPKIN_ADDR = "http://127.0.0.1:9411";
//    String SEATA_ADDR = "127.0.0.1:8091";
    String NACOS_ADDR = "127.0.0.1:8848";
    String NACOS_OBJECT_NAME = "fw.ee";
    String NACOS_CONFIG_FORMAT = "yaml";
    String NACOS_CONFIG_JSON_FORMAT = "json";
    String NACOS_CONFIG_REFRESH = "true";
    String NACOS_CONFIG_GROUP = "FW_EE";
//    String NACOS_SEATA_GROUP = "SEATA_GROUP";

    static String dataId(String appName) {
        return appName + "." + "yaml";
    }

    static String dataId(String appName, String profile) {
        return dataId(appName, profile, "yaml");
    }

    static String dataId(String appName, String profile, String format) {
        return appName + "-" + profile + "." + format;
    }

    static String sharedDataId() {
        return NACOS_OBJECT_NAME + ".yaml";
    }

    static String sharedDataId(String profile) {
        return NACOS_OBJECT_NAME + "-" + profile + "." + "yaml";
    }

    static String sharedDataIds(String profile) {
        return NACOS_OBJECT_NAME + ".yaml," + NACOS_OBJECT_NAME + "-" + profile + "." + "yaml";
    }
}
