package io.oeid.mogakgo.domain.auth.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.auth0.jwt.exceptions.JWTVerificationException;
import io.oeid.mogakgo.core.properties.JwtProperties;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("기능 테스트: JwtHelper")
class JwtHelperTest {

    private static final JwtProperties JWT_PROPERTIES;
    private static final JwtHelper JWT_HELPER;

    static {
        JWT_PROPERTIES = new JwtProperties();
        JWT_PROPERTIES.setHeader("Authorization");
        JWT_PROPERTIES.setIssuer("MoGakGo");
        JWT_PROPERTIES.setClientSecret("mogakgo");
        JWT_PROPERTIES.setAccessTokenExpiryHour(1);
        JWT_PROPERTIES.setRefreshTokenExpiryHour(2);
        JWT_HELPER = new JwtHelper(JWT_PROPERTIES);
        ;
    }

    @Test
    void 토큰_발급() {
        // Arrange
        long expectedUserId = 1L;
        String[] expectedRoles = {"ROLE_USER"};
        // Act
        var actualResult = JWT_HELPER.sign(expectedUserId, expectedRoles);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .hasFieldOrPropertyWithValue("userId", expectedUserId)
            .hasFieldOrProperty("accessToken")
            .hasFieldOrProperty("refreshToken")
            .hasFieldOrProperty("refreshTokenExpirySeconds");
    }

    @Test
    void 토큰_재발급(){
        // Arrange
        long expectedUserId = 1L;
        String[] expectedRoles = {"ROLE_USER"};
        String expectedRefreshToken = "test_refresh_token";
        // Act
        var actualResult = JWT_HELPER.sign(expectedUserId, expectedRoles, expectedRefreshToken);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .hasFieldOrPropertyWithValue("userId", expectedUserId)
            .hasFieldOrProperty("accessToken")
            .hasFieldOrPropertyWithValue("refreshToken", expectedRefreshToken)
            .hasFieldOrProperty("refreshTokenExpirySeconds");
    }

    @Test
    void 토큰_검증_성공(){
        // Arrange
        long expectedUserId = 1L;
        String[] expectedRoles = {"ROLE_USER"};
        var accessToken = JWT_HELPER.sign(expectedUserId, expectedRoles).getAccessToken();
        // Act
        var actualResult = JWT_HELPER.verify(accessToken);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .containsKeys("userId", "roles", "exp", "iat", "iss");
        assertThat(actualResult.get("userId").asLong()).isEqualTo(expectedUserId);
        assertThat(actualResult.get("roles").asArray(String.class)).isEqualTo(expectedRoles);
    }

    @Test
    void 토큰_검증_실패(){
        // Arrange
        long expectedUserId = 1L;
        String[] expectedRoles = {"ROLE_USER"};
        var refreshToken = JWT_HELPER.sign(expectedUserId, expectedRoles).getRefreshToken();
        // Act & Assert
        assertThatThrownBy(() -> JWT_HELPER.verify(refreshToken))
            .isInstanceOf(JWTVerificationException.class)
            .hasMessageContaining("Invalid token");
    }

    @Test
    void 만료_토큰_검증(){
        // Arrange
        long expectedUserId = 1L;
        String[] expectedRoles = {"ROLE_USER"};
        var accessToken = JWT_HELPER.sign(expectedUserId, expectedRoles).getAccessToken();
        // Act
        var actualResult = JWT_HELPER.verifyWithoutExpiry(accessToken);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .containsKeys("userId", "roles", "exp", "iat", "iss");
        assertThat(actualResult.get("userId").asLong()).isEqualTo(expectedUserId);
        assertThat(actualResult.get("roles").asArray(String.class)).isEqualTo(expectedRoles);
    }

    @Test
    void 리프레시_토큰_검증() {
        // Arrange
        long expectedUserId = 1L;
        String[] expectedRoles = {"ROLE_USER"};
        var refreshToken = JWT_HELPER.sign(expectedUserId, expectedRoles).getRefreshToken();
        // Act
        var actualResult = JWT_HELPER.verifyRefreshToken(refreshToken);
        // Assert
        assertThat(actualResult)
            .isNotNull()
            .containsKeys("exp", "iat", "iss");
    }

}