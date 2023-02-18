package org.wlpiaoyi.framework.ee.utils.exception;

import lombok.Getter;
import lombok.NonNull;
import org.wlpiaoyi.framework.ee.utils.status.File;

import java.util.Set;

/**
 * @author wlpia
 */
@Getter
public class FileException extends BusinessException {

    private Set<java.io.File> files;
    public FileException(@NonNull File fileStatus, Set<java.io.File> files) {
        super(fileStatus);
        this.files = files;
    }

}