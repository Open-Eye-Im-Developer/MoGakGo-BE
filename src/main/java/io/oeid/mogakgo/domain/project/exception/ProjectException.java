package io.oeid.mogakgo.domain.project.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class ProjectException extends CustomException {

    public ProjectException(ErrorCode errorCode) {
        super(errorCode);
    }
}
