package org.wlpiaoyi.framework.ee.utils.exception;

import org.wlpiaoyi.framework.ee.utils.status.Status;
import org.wlpiaoyi.framework.utils.ValueUtils;

/**
 * @author wlpia
 */
public class CatchException extends org.wlpiaoyi.framework.utils.exception.CatchException {


    public CatchException(Status status) {
        super(status.getName());
        this.code = status.getIndex();
        this.message = status.getName();
    }

    public CatchException(Status status, String message) {
        super(status.getName());
        this.code = status.getIndex();
        this.message = ValueUtils.isBlank(message) ? status.getName() : message;
    }

    public CatchException(String message) {
        super(message);
        this.code = 10000;
        this.message = message;
    }


    public CatchException(String message, Throwable cause) {
        super(10000, message, cause);
        int code = 10000;
        if(cause instanceof BusinessException){
            code = ((BusinessException) cause).getCode();
        }
        this.code = code;
        this.message = message;
    }

    public CatchException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public CatchException(int code, String message, Throwable cause) {
        super(code, message, cause);
        this.code = code;
        this.message = message;
    }
}

