package org.wlpiaoyi.framework.ee.utils.exception;

import lombok.Getter;
import org.wlpiaoyi.framework.ee.utils.status.Status;
import org.wlpiaoyi.framework.utils.ValueUtils;

/**
 * @author wlpia
 */
@Getter
public class BusinessException extends org.wlpiaoyi.framework.utils.exception.BusinessException {


    public BusinessException(Status status) {
        super(status.getName());
        this.code = status.getIndex();
        this.message = status.getName();
    }

    public BusinessException(Status status, String message) {
        super(status.getName());
        this.code = status.getIndex();
        this.message = ValueUtils.isBlank(message) ? status.getName() : message;
    }

    public BusinessException(String message) {
        super(message);
        this.code = 10000;
        this.message = message;
    }


    public BusinessException(String message, Throwable cause) {
        super(500, message, cause);
        int code = 500;
        if(cause instanceof BusinessException){
            code = ((BusinessException) cause).getCode();
        }
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(code, message, cause);
        this.code = code;
        this.message = message;
    }

}
