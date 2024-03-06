package io.oeid.mogakgo.domain.achievement.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class UserAchievementException extends CustomException {

    public UserAchievementException(ErrorCode errorCode) {
        super(errorCode);
    }
}
