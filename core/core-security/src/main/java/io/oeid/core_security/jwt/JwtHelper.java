package io.oeid.core_security.jwt;

import static com.auth0.jwt.JWT.create;
import static com.auth0.jwt.JWT.require;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.oeid.core_security.properties.JwtProperties;
import io.oeid.domain_user.infrastructure.UserJpaRepository;
import java.util.Date;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class JwtHelper {

    public static final String USER_ID_STR = "userId";
    public static final String ROLES_STR = "roles";
    private static final Long HOUR_TO_MILLIS = 3600000L;

    private final String issuer;
    private final long accessTokenExpirySeconds;
    private final long refreshTokenExpirySeconds;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final UserJpaRepository userRepository;

    public JwtHelper(JwtProperties jwtProperties, UserJpaRepository userRepository) {
        this.issuer = jwtProperties.getIssuer();
        this.accessTokenExpirySeconds = hoursToMillis(jwtProperties.getAccessTokenExpiryHour());
        this.refreshTokenExpirySeconds = hoursToMillis(jwtProperties.getRefreshTokenExpiryHour());
        this.algorithm = Algorithm.HMAC256(jwtProperties.getClientSecret());
        this.jwtVerifier = require(algorithm).withIssuer(issuer).build();
        this.userRepository = userRepository;
    }

    private static long hoursToMillis(int hour) {
        return hour * HOUR_TO_MILLIS;
    }

    private static Date calculateExpirySeconds(Date now, long tokenExpirySeconds) {
        return new Date(now.getTime() + tokenExpirySeconds);
    }

    public JwtToken sign(long userId, String[] roles) {
        Date now = new Date();
        String accessToken = create()
            .withIssuer(issuer)
            .withIssuedAt(now)
            .withExpiresAt(calculateExpirySeconds(now, accessTokenExpirySeconds))
            .withClaim(USER_ID_STR, userId)
            .withArrayClaim(ROLES_STR, roles)
            .sign(algorithm);
        Date refreshTokenExpiryDate = calculateExpirySeconds(now, refreshTokenExpirySeconds);
        String refreshToken = create()
            .withIssuer(issuer)
            .withIssuedAt(now)
            .withExpiresAt(refreshTokenExpiryDate)
            .sign(algorithm);
        return JwtToken.of(userId, accessToken, refreshToken, refreshTokenExpiryDate);
    }

    public Map<String, Claim> verify(String token)
        throws JWTVerificationException {
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        var claims = decodedJWT.getClaims();
        String userId = claims.get(USER_ID_STR).asString();
        var roles = claims.get(ROLES_STR).asArray(String.class);
        if (userId == null || roles == null) {
            throw new JWTVerificationException("Invalid token");
        }
        userRepository.findById(Long.parseLong(userId))
            .orElseThrow(() -> new JWTVerificationException("Invalid token"));
        return claims;
    }
}
