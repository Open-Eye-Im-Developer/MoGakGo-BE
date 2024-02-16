package io.oeid.mogakgo.exception;

import static io.oeid.mogakgo.exception.code.ErrorCode400.INVALID_INPUT_VALUE;
import static io.oeid.mogakgo.exception.code.ErrorCode400.PATH_PARAMETER_BAD_REQUEST;
import static io.oeid.mogakgo.exception.code.ErrorCode500.INTERNAL_SERVER_ERROR;

import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.oeid.mogakgo.exception.exceptionClass.CustomException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<ErrorResponse> handleCustomException(
        CustomException e, HttpServletRequest request
    ) {
        return ErrorResponse.from(e.getErrorCode());
    }

    @ExceptionHandler(value = {
        BindException.class,
        MethodArgumentNotValidException.class
    })
    protected ResponseEntity<ErrorResponse> validationException(
        BindException e,
        HttpServletRequest request
    ) {
        BindingResult bindingResult = e.getBindingResult();

        StringBuilder builder = new StringBuilder();
        for (FieldError fieldError : bindingResult.getFieldErrors()) {
            builder.append("[");
            builder.append(fieldError.getField());
            builder.append("](은)는 ");
            builder.append(fieldError.getDefaultMessage());
            builder.append(" 입력된 값: [");
            builder.append(fieldError.getRejectedValue());
            builder.append("]");
            builder.append(System.lineSeparator());
        }

        return ErrorResponse.ofWithErrorMessage(INVALID_INPUT_VALUE, builder.toString());
    }

    @ExceptionHandler(value = {
        MissingPathVariableException.class,
        MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<ErrorResponse> missingPathVariableException(
        Exception e, HttpServletRequest request
    ) {
        return ErrorResponse.from(PATH_PARAMETER_BAD_REQUEST);
    }

    @ExceptionHandler(value = Exception.class)
    protected ResponseEntity<ErrorResponse> handleException(
        Exception e, HttpServletRequest request
    ) {
        return ErrorResponse.from(INTERNAL_SERVER_ERROR);
    }
}
