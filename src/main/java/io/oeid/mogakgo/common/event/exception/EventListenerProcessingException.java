package io.oeid.mogakgo.common.event.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class EventListenerProcessingException extends CustomException {

    public EventListenerProcessingException(ErrorCode errorCode) {
        super(errorCode);
    }
}
