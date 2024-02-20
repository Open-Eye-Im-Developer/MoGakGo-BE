package io.oeid.mogakgo.domain.matching.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class MatchingException extends CustomException {

    public MatchingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
