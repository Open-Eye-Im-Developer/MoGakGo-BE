package io.oeid.core_security.jwt;

import static com.auth0.jwt.JWT.create;
import static com.auth0.jwt.JWT.require;

import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import java.util.Date;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;

public class JwtHelper {

    private static final String USER_ID_STR = "userId";
    private static final String ROLES_STR = "roles";
    private static final Long HOUR_TO_MILLIS = 3600000L;

    private final String issuer;
    private final long accessTokenExpirySeconds;
    private final long refreshTokenExpirySeconds;
    private final Algorithm algorithm;
    private final JWTVerifier jwtVerifier;
    private final UserDetailsService userDetailsService;

    public JwtHelper(String issuer, int accessTokenExpiryHours, int refreshTokenExpiryHours,
        String clientSecret, UserDetailsService userDetailsService) {
        this.issuer = issuer;
        this.accessTokenExpirySeconds = hoursToMillis(accessTokenExpiryHours);
        this.refreshTokenExpirySeconds = hoursToMillis(refreshTokenExpiryHours);
        this.algorithm = Algorithm.HMAC256(clientSecret);
        this.jwtVerifier = require(algorithm).withIssuer(issuer).build();
        this.userDetailsService = userDetailsService;
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

    public Authentication verify(String token) throws JWTVerificationException {
        DecodedJWT decodedJWT = jwtVerifier.verify(token);
        var claims = decodedJWT.getClaims();
        String userId = claims.get(USER_ID_STR).asString();
        var roles = claims.get(ROLES_STR).asArray(String.class);
        if (userId == null || roles == null) {
            throw new JWTVerificationException("Invalid token");
        }
        var userDetails = userDetailsService.loadUserByUsername(userId);
        return new UsernamePasswordAuthenticationToken(
            userDetails, "", userDetails.getAuthorities());
    }
}
