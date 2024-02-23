package io.oeid.mogakgo.common.swagger.template;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerAuthErrorExamples;
import io.oeid.mogakgo.domain.auth.presentation.dto.req.AuthReissueRequest;
import io.oeid.mogakgo.domain.auth.presentation.dto.res.AuthAccessTokenResponse;
import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Auth", description = "인증 관련 API")
@SuppressWarnings("unused")
public interface AuthSwagger {

    @Operation(summary = "토큰 재발급", description = "Access Token을 재발급 받을 때 사용하는 API")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "토큰 재발급 성공",
            content = @Content(schema = @Schema(implementation = AuthAccessTokenResponse.class))),
        @ApiResponse(responseCode = "401", description = "재발급 토큰 인증정보가 유효하지 않음",
            content = @Content(
                mediaType = APPLICATION_JSON_VALUE,
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E010201", value = SwaggerAuthErrorExamples.AUTH_MISSING_CREDENTIALS)))
    })
    ResponseEntity<AuthAccessTokenResponse> reissue(
        @Parameter(in = ParameterIn.HEADER, hidden = true) String accessToken,
        AuthReissueRequest refreshToken);
}
