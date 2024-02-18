package io.oeid.mogakgo.common.swagger.template;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerCertErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.cert.presentation.dto.req.UserRegionCertAPIReq;
import io.oeid.mogakgo.domain.cert.presentation.dto.req.UserRegionInfoAPIReq;
import io.oeid.mogakgo.domain.cert.presentation.dto.res.UserRegionCertAPIRes;
import io.oeid.mogakgo.domain.cert.presentation.dto.res.UserRegionInfoAPIRes;
import io.oeid.mogakgo.exception.dto.ErrorResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;

@Tag(name = "Cert", description = "동네 인증 관련 API")
public interface CertSwagger {

    @Operation(summary = "GPS에 대한 법정구역코드 응답", description = "사용자의 GPS 좌표의 법정구역코드를 요청할 때 사용하는 API"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "법정구역코드 요청 성공",
            content = @Content(schema = @Schema(implementation = UserRegionInfoAPIRes.class))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E080101", value = SwaggerCertErrorExamples.INVALID_CERT_REGION))),
        @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<UserRegionInfoAPIRes> getUserRegionInfoByGPS(
        @Parameter(hidden = true) Long userId,
        UserRegionInfoAPIReq request
    );

    @Operation(summary = "동네 인증 완료 응답", description = "동네 인증 완료를 요청할 때 사용하는 API"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "동네 인증 요청 성공",
            content = @Content(schema = @Schema(implementation = Long.class))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E080101", value = SwaggerCertErrorExamples.INVALID_CERT_REGION))),
        @ApiResponse(responseCode = "401", description = "동네 인증 권한이 없음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E070201", value = SwaggerCertErrorExamples.INVALID_CERT_INFO))),
        @ApiResponse(responseCode = "404", description = "요청한 유저가 존재하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E020301", value = SwaggerUserErrorExamples.USER_NOT_FOUND))),
    })
    ResponseEntity<UserRegionCertAPIRes> certificateNeighborhood(
        @Parameter(hidden = true) Long userId,
        UserRegionCertAPIReq request
    );
}
