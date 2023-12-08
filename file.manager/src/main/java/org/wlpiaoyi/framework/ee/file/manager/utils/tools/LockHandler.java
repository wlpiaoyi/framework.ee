package org.wlpiaoyi.framework.ee.file.manager.utils.tools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.wlpiaoyi.framework.utils.web.lock.AbstractLockHandler;
import org.wlpiaoyi.framework.utils.web.lock.Lock;

import java.nio.charset.StandardCharsets;

/**
 * {@code @author:}         wlpiaoyi
 * {@code @description:}    TODO
 * {@code @date:}           2023/11/15 1:15
 * {@code @version:}:       1.0
 */
@Component
public class LockHandler extends AbstractLockHandler {
    @Override
    protected boolean setRedisLock(Lock lock, long l) {
        return false;
    }

    @Override
    protected boolean hasRedisLock(Lock lock) {
        return false;
    }

    @Override
    protected Long delRedisLock(Lock lock) {
        return null;
    }

//    @Autowired
//    private RedisTemplate redisTemplate;
//
//    protected boolean setRedisLock(Lock lock, long lockExpireTime){
//        RedisTemplate<String, Boolean> rt = this.redisTemplate;
//        boolean res = rt.execute(
//                (RedisCallback<Boolean>) connection -> connection.setNX(
//                        lock.getId().getBytes(StandardCharsets.UTF_8),
//                        lock.getId().getBytes(StandardCharsets.UTF_8)));
//        if(res){
//            res = redisTemplate.getConnectionFactory().getConnection().pExpire(
//                    lock.getId().getBytes(StandardCharsets.UTF_8),
//                    lockExpireTime);
//        }
//        return res;
//    }
//
//    protected boolean hasRedisLock(Lock lock){
//        RedisTemplate<String, Boolean> rt = this.redisTemplate;
//        return rt.execute(
//                (RedisCallback<Boolean>) connection -> {
//                    byte[] bytes = connection.getRange(
//                            lock.getId().getBytes(StandardCharsets.UTF_8),
//                            0,0);
//                    if(ValueUtils.isBlank(bytes)) return false;
//                    return true;
//                });
//    }
//
//    protected Long delRedisLock(Lock lock){
//        RedisTemplate<String, Long> rt = this.redisTemplate;
//        Long count = rt.execute(
//                (RedisCallback<Long>) connection -> connection.del(lock.getId().getBytes(StandardCharsets.UTF_8)));
//        if(count == null)
//            count = 0L;
//        return count;
//    }
}
