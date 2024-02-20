package io.oeid.mogakgo.common.swagger.template;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.oeid.mogakgo.domain.auth.presentation.dto.res.AuthLoginUrlResponse;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.core.user.OAuth2User;

@Tag(name = "OAuth2", description = "OAuth2 관련 API")
@SuppressWarnings("unused")
public interface OAuth2Swagger {

    @Operation(summary = "로그인 URL 반환", description = "로그인 URL을 반환하는 API")
    @ApiResponse(responseCode = "200", description = "로그인 URL 반환 성공",
        content = @Content(
            mediaType = APPLICATION_JSON_VALUE,
            schema = @Schema(implementation = AuthLoginUrlResponse.class)))
    ResponseEntity<AuthLoginUrlResponse> login();

    @Hidden
    void loginSuccess(
        OAuth2User oAuth2User, HttpServletResponse response) throws IOException;
}
