package io.oeid.mogakgo.exception.dto;

import io.oeid.mogakgo.exception.code.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.ResponseEntity;

@Schema(description = "에러 응답")
@Getter
@Builder
public class ErrorResponse {

    private final LocalDateTime timestamp = LocalDateTime.now();
    private final int statusCode;
    private final String code;
    private final String message;

    public static ResponseEntity<ErrorResponse> from(
        ErrorCode errorCode
    ) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ErrorResponse.builder()
                .statusCode(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build()
            );
    }

    public static ResponseEntity<ErrorResponse> ofWithErrorMessage(
        ErrorCode errorCode, String message
    ) {
        return ResponseEntity
            .status(errorCode.getHttpStatus())
            .body(ErrorResponse.builder()
                .statusCode(errorCode.getHttpStatus().value())
                .code(errorCode.getCode())
                .message(message)
                .build()
            );
    }
}
