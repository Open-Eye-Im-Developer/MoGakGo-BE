package io.oeid.mogakgo.domain.auth.application;

import com.auth0.jwt.interfaces.Claim;
import io.oeid.mogakgo.domain.auth.application.dto.res.AuthLoginResponse;
import io.oeid.mogakgo.domain.auth.application.dto.res.AuthOAuth2Response;
import io.oeid.mogakgo.domain.auth.application.dto.res.AuthReissueResponse;
import io.oeid.mogakgo.domain.auth.exception.AuthException;
import io.oeid.mogakgo.domain.auth.jwt.JwtHelper;
import io.oeid.mogakgo.domain.auth.jwt.JwtRedisDao;
import io.oeid.mogakgo.domain.auth.jwt.JwtToken;
import io.oeid.mogakgo.domain.auth.util.GithubOAuth2Manager;
import io.oeid.mogakgo.exception.code.ErrorCode401;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@RequiredArgsConstructor
@Service
public class AuthService {

    private final JwtRedisDao jwtRedisDao;
    private final JwtHelper jwtHelper;
    private final AuthUserService authUserService;
    private final GithubOAuth2Manager githubOAuth2Manager;

    public AuthReissueResponse reissue(String expiredAccessToken, String refreshToken) {
        log.info("reissue expiredAccessToken: {}", expiredAccessToken);
        log.info("reissue refreshToken: {}", refreshToken);
        expiredAccessToken = expiredAccessToken.substring(7); // remove "Bearer " (7 characters)
        String verifyRefreshToken = jwtRedisDao.getRefreshTokenByAccessToken(expiredAccessToken);
        log.info("verifyRefreshToken: {}", verifyRefreshToken);
        if (!refreshToken.equals(verifyRefreshToken)) {
            log.info("refreshToken not matched");
            throw new AuthException(ErrorCode401.AUTH_MISSING_CREDENTIALS);
        }
        String accessToken = generateAccessToken(expiredAccessToken);
        int refreshTokenExpirySeconds = calculateRefreshTokenExpirySeconds(refreshToken);
        jwtRedisDao.saveTokens(accessToken, refreshToken, refreshTokenExpirySeconds);
        return AuthReissueResponse.from(accessToken);
    }

    @Transactional
    public AuthLoginResponse loginViaGithubCode(String code) {
        verifyCode(code);
        var githubAccessToken = githubOAuth2Manager.getAccessToken(code);
        var githubUserInfo = githubOAuth2Manager.getGithubUserInfo(githubAccessToken);
        var userOAuth2Response = authUserService.manageOAuth2User(githubUserInfo);
        var jwtToken = generateJwtToken(userOAuth2Response);
        return AuthLoginResponse.of(userOAuth2Response, jwtToken);
    }

    private String generateAccessToken(String expiredAccessToken) {
        Map<String, Claim> claims = jwtHelper.verifyWithoutExpiry(expiredAccessToken);
        long userId = claims.get(JwtHelper.USER_ID_STR).asLong();
        String[] roles = claims.get(JwtHelper.ROLES_STR).asArray(String.class);
        return jwtHelper.sign(userId, roles, expiredAccessToken).getAccessToken();
    }

    private int calculateRefreshTokenExpirySeconds(String refreshToken) {
        Map<String, Claim> claims = jwtHelper.verifyRefreshToken(refreshToken);
        return (int) (claims.get("exp").asLong() - System.currentTimeMillis() / 1000);
    }

    private void verifyCode(String code) {
        if (code == null || code.isBlank()) {
            throw new AuthException(ErrorCode401.AUTH_MISSING_CREDENTIALS);
        }
    }

    private JwtToken generateJwtToken(AuthOAuth2Response response) {
        long userId = response.getUserId();
        String[] roles = response.getAuthorities().stream().map(GrantedAuthority::getAuthority)
            .toArray(String[]::new);
        var jwtToken = jwtHelper.sign(userId, roles);
        jwtRedisDao.saveTokens(jwtToken.getAccessToken(), jwtToken.getRefreshToken());
        return jwtToken;
    }
}