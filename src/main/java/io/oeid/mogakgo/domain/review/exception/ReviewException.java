package io.oeid.mogakgo.domain.review.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class ReviewException extends CustomException {

    public ReviewException(ErrorCode errorCode) {
        super(errorCode);
    }
}
