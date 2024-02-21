package io.oeid.mogakgo.domain.profile.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class ProfileCardLikeException extends CustomException {

    public ProfileCardLikeException(ErrorCode errorCode) {
        super(errorCode);
    }
}
