package org.wlpiaoyi.framework.ee.utils.filter.idempotence;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.wlpiaoyi.framework.ee.utils.filter.FilterSupport;
import org.wlpiaoyi.framework.ee.utils.loader.IdempotenceLoader;

import jakarta.servlet.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 幂等性
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    幂等性
 * {@code @date:}           2023/2/23 0:06
 * {@code @version:}:       1.0
 */
@Slf4j
@Order(Integer.MIN_VALUE)
//@Component
public abstract class BaseIdempotenceFilter extends IdempotenceLoader implements Filter, FilterSupport {

    public abstract IdempotenceMoon getIdempotenceMoon ();

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
     * 判读是否限制重复请求
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
                    return false;
                }
            }
        }
        if(Math.abs(System.currentTimeMillis() - timer) > DURI_TIMER){
            IDEMPOTENCE_TIMER_MAP.put(key, System.currentTimeMillis());
            return false;
        }
        IDEMPOTENCE_TIMER_MAP.put(key, System.currentTimeMillis());
        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        String uri = this.getRequestURI(servletRequest);
        log.debug("doFilter class:" + this.getClass().getName() + "uri:" + uri);
        //进入
        boolean canDoFilter = false;
        try{
            if(!this.getConfigModel().checkIdempotencePatterns(uri)
               && !IDEMPOTENCE_URI_SET.contains(uri)){
                canDoFilter = true;
                return;
            }
            if(!isIdempotence(this.getIdempotenceMoon().getKey(servletRequest))){
                canDoFilter = true;
                return;
            }
            canDoFilter = false;
            servletResponse.getOutputStream().close();
            servletRequest.getInputStream().close();
        }catch (Exception e){
            canDoFilter = false;
            log.error("IdempotenceFilter.doFilter:", e);
        }finally {
            if(canDoFilter){
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }


    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }
}
