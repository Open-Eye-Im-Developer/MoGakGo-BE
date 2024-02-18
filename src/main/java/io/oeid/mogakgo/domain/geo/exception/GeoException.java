package io.oeid.mogakgo.domain.geo.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class GeoException extends CustomException {

    public GeoException(ErrorCode errorCode) {
        super(errorCode);
    }

}
