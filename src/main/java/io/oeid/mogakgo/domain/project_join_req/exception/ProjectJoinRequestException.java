package io.oeid.mogakgo.domain.project_join_req.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class ProjectJoinRequestException extends CustomException {

    public ProjectJoinRequestException(ErrorCode errorCode) {
        super(errorCode);
    }
}
