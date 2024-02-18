package io.oeid.mogakgo.common.swagger.template;

import io.oeid.mogakgo.core.properties.swagger.error.SwaggerCertErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerGeoErrorExamples;
import io.oeid.mogakgo.core.properties.swagger.error.SwaggerUserErrorExamples;
import io.oeid.mogakgo.domain.cert.presentation.dto.req.UserRegionCertAPIReq;
import io.oeid.mogakgo.domain.cert.presentation.dto.res.UserRegionCertAPIRes;
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
@SuppressWarnings("unused")
public interface CertSwagger {

    @Operation(summary = "동네 인증 완료 응답", description = "동네 인증 완료를 요청할 때 사용하는 API"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "동네 인증 요청 성공",
            content = @Content(schema = @Schema(implementation = UserRegionCertAPIRes.class))),
        @ApiResponse(responseCode = "400", description = "요청한 데이터가 유효하지 않음",
            content = @Content(
                mediaType = "application/json",
                schema = @Schema(implementation = ErrorResponse.class),
                examples = @ExampleObject(name = "E080101", value = SwaggerGeoErrorExamples.INVALID_SERVICE_REGION))),
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
