package io.oeid.mogakgo.domain.outbox.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class OutboxException extends CustomException {

    public OutboxException(ErrorCode errorCode) {
        super(errorCode);
    }
}
