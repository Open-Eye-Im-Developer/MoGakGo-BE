package io.oeid.mogakgo.domain.auth.application;

import com.auth0.jwt.interfaces.Claim;
import io.oeid.mogakgo.domain.auth.application.dto.res.AuthReissueResponse;
import io.oeid.mogakgo.domain.auth.exception.AuthException;
import io.oeid.mogakgo.domain.auth.jwt.JwtHelper;
import io.oeid.mogakgo.domain.auth.jwt.JwtRedisDao;
import io.oeid.mogakgo.exception.code.ErrorCode401;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtRedisDao jwtRedisDao;
    private final JwtHelper jwtHelper;

    public AuthReissueResponse reissue(String expiredAccessToken, String refreshToken) {
        String verifyRefreshToken = jwtRedisDao.getRefreshTokenByAccessToken(expiredAccessToken);
        if (!refreshToken.equals(verifyRefreshToken)) {
            throw new AuthException(ErrorCode401.AUTH_MISSING_CREDENTIALS);
        }
        String accessToken = generateAccessToken(expiredAccessToken);
        int refreshTokenExpirySeconds = calculateRefreshTokenExpirySeconds(refreshToken);
        jwtRedisDao.saveTokens(accessToken, refreshToken, refreshTokenExpirySeconds);
        return AuthReissueResponse.from(accessToken);
    }

    private String generateAccessToken(String expiredAccessToken) {
        Map<String, Claim> claims = jwtHelper.verifyWithoutExpiry(expiredAccessToken);
        long userId = claims.get(JwtHelper.USER_ID_STR).asLong();
        String[] roles = claims.get(JwtHelper.ROLES_STR).asArray(String.class);
        return jwtHelper.sign(userId, roles, expiredAccessToken).getAccessToken();
    }

    private int calculateRefreshTokenExpirySeconds(String refreshToken) {
        Map<String, Claim> claims = jwtHelper.verify(refreshToken);
        return (int) ((claims.get("exp").asLong() - System.currentTimeMillis()) / 1000);
    }
}
