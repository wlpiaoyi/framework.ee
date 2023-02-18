package org.wlpiaoyi.framework.ee.utils.loader;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.wlpiaoyi.framework.ee.utils.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.advice.AnnotationPathUtils;
import org.wlpiaoyi.framework.ee.utils.advice.handle.Idempotence;

import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/2/18 18:41
 * {@code @version:}:       1.0
 */
public class IdempotenceLoader {

    /**
     * 要求幂等URI
     */
    protected static final Set<String> IDEMPOTENCE_URI_SET = new HashSet<>();

    protected static int SECTION_TIMER = 20 * 60;
    protected static int DURI_TIMER = 3;

    /**
     * 加载数据
     * 找出需要幂等的URI
     * @param applicationContext
     * @throws BeansException
     */
    static void load(ApplicationContext applicationContext) throws BeansException {
        ConfigModel configModel = applicationContext.getBean(ConfigModel.class);
        DURI_TIMER = configModel.getIdempotenceDuriTime() * 1000;
        SECTION_TIMER = configModel.getIdempotenceSectionTime() * 1000;
        Set<String> pathSet = new HashSet<>();
        AnnotationPathUtils.iteratorAllPath1(applicationContext, (value, path1) -> {
            path1 = AnnotationPathUtils.checkMappingValue(path1);
            Method[] methods = value.getClass().getMethods();
            AnnotationPathUtils.iteratorAllPath2(methods, path1, method -> {
                Idempotence idempotence = AnnotationUtils.findAnnotation(method, Idempotence.class);
                if (idempotence == null) {
                    return true;
                }
                return false;
            }, path -> {
                if(pathSet.contains(path)){
                    return 0;
                }
                pathSet.add(path);
                return 0;
            });

        });
        synchronized (IDEMPOTENCE_URI_SET){
            IDEMPOTENCE_URI_SET.clear();
            IDEMPOTENCE_URI_SET.addAll(pathSet);
        }
    }
}
