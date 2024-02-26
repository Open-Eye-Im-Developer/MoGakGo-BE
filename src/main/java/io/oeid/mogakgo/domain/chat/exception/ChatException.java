package io.oeid.mogakgo.domain.chat.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class ChatException extends CustomException {

    public ChatException(ErrorCode errorCode) {
        super(errorCode);
    }
}
