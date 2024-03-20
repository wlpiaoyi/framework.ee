package org.wlpiaoyi.framework.ee.openai.azure.run;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/8/22 10:30
 * {@code @version:}:       1.0
 */
public interface Run {
    int run(String[] args) throws Exception;
    int getTag();
    String getDescribe();
}
