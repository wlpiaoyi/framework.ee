package org.wlpiaoyi.framework.ee.utils.IdWork;

public class IdWorker extends org.wlpiaoyi.framework.utils.snowflake.IdWorker {

    private static IdWorker xIdWorker;

    static {
        IdWorker.TWEPOCH = 1633017600000L;
    }

    private IdWorker(long workerId, long datacenterId) {
        super(workerId, datacenterId);
    }

    public static IdWorker shared(){
        if(xIdWorker == null) {
            synchronized (IdWorker.class){
                if(xIdWorker == null) xIdWorker = new IdWorker(1,1);
            }
        }
        return xIdWorker;
    }

}
