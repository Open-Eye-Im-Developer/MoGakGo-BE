package io.oeid.mogakgo.domain.cert.exception;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.oeid.mogakgo.exception.exception_class.CustomException;

public class CertException extends CustomException {

    public CertException(ErrorCode errorCode) {
        super(errorCode);
    }
}
