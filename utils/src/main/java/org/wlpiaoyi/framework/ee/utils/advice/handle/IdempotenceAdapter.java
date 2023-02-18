package org.wlpiaoyi.framework.ee.utils.advice.handle;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.wlpiaoyi.framework.ee.utils.ConfigModel;
import org.wlpiaoyi.framework.ee.utils.loader.IdempotenceLoader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * 幂等性
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    幂等性
 * {@code @date:}           2023/2/16 11:56
 * {@code @version:}:       1.0
 */
@Slf4j
public abstract class IdempotenceAdapter extends IdempotenceLoader implements HandlerInterceptor {

    /**
     * 幂等URI的时间记录
     */
    private static final Map<String, Long> IDEMPOTENCE_TIMER_MAP = new HashMap<>();

    static {
        new Thread(() -> {
            while (true){
                try{
                    Thread.sleep(SECTION_TIMER);
                    Set<Map.Entry<String, Long>> setEntries = new HashSet<>();
                    synchronized (IDEMPOTENCE_TIMER_MAP){
                        setEntries.addAll(IDEMPOTENCE_TIMER_MAP.entrySet());
                    }
                    Set<String> removes = new HashSet<>();
                    long timer = System.currentTimeMillis();
                    for(Map.Entry<String, Long> entry : setEntries){
                        if(Math.abs(entry.getValue() - timer) > DURI_TIMER){
                            removes.add(entry.getKey());
                        }
                    }
                    synchronized (IDEMPOTENCE_TIMER_MAP){
                        for(String key : removes){
                            IDEMPOTENCE_TIMER_MAP.remove(key);
                        }
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }


    /**
     * 判读是否重复请求
     * @param key
     * @return
     */
    public static boolean isIdempotence(String key){
        Long timer = IDEMPOTENCE_TIMER_MAP.get(key);
        if(timer == null){
            synchronized (IDEMPOTENCE_TIMER_MAP){
                timer = IDEMPOTENCE_TIMER_MAP.get(key);
                if(timer == null){
                    IDEMPOTENCE_TIMER_MAP.put(key, System.currentTimeMillis());
                    return true;
                }
            }
        }
        if(Math.abs(System.currentTimeMillis() - timer) > DURI_TIMER){
            IDEMPOTENCE_TIMER_MAP.put(key, System.currentTimeMillis());
            return true;
        }
        IDEMPOTENCE_TIMER_MAP.put(key, System.currentTimeMillis());
        return false;
    }

    public abstract IdempotenceMoon getIdempotenceMoon ();
    public abstract ConfigModel getConfigModel();

    /**
     * This implementation always returns {@code true}.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        if(this.getConfigModel() != null){
            if(!this.getConfigModel().checkIdempotencePatterns(request.getRequestURI())){
                return true;
            }
        }

        if(!IDEMPOTENCE_URI_SET.contains(request.getRequestURI())){
            return true;
        }
        return IdempotenceAdapter.isIdempotence(this.getIdempotenceMoon().getKey(request, response, handler));
    }

//    /**
//     * This implementation is empty.
//     */
//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
//                           @Nullable ModelAndView modelAndView) throws Exception {
//    }
//
//    /**
//     * This implementation is empty.
//     */
//    @Override
//    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
//                                @Nullable Exception ex) throws Exception {
//    }
//
//    /**
//     * This implementation is empty.
//     */
//    @Override
//    public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,
//                                               Object handler) throws Exception {
//    }
}
