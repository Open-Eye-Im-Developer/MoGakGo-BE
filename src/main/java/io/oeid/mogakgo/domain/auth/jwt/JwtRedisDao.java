package io.oeid.mogakgo.domain.auth.jwt;

import io.oeid.mogakgo.core.properties.JwtProperties;
import io.oeid.mogakgo.domain.auth.exception.AuthException;
import io.oeid.mogakgo.exception.code.ErrorCode401;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class JwtRedisDao {

    private final RedisTemplate<String, String> redisTemplate;
    private final int refreshExpireHour;

    public JwtRedisDao(StringRedisTemplate redisTemplate, JwtProperties jwtProperties) {
        this.redisTemplate = redisTemplate;
        this.refreshExpireHour = jwtProperties.getRefreshTokenExpiryHour();
    }

    @Transactional
    public void saveTokens(String accessToken, String refreshToken) {
        redisTemplate.opsForValue()
            .set(accessToken, refreshToken, refreshExpireHour, TimeUnit.HOURS);
    }

    @Transactional
    public void saveTokens(String accessToken, String refreshToken, int expireHour) {
        redisTemplate.opsForValue()
            .set(accessToken, refreshToken, expireHour, TimeUnit.SECONDS);
    }

    @Transactional(readOnly = true)
    public String getRefreshTokenByAccessToken(String accessToken) {
        var result = Optional.ofNullable(redisTemplate.opsForValue().get(accessToken));
        return result.orElseThrow(() -> {
            log.debug("refreshToken not found");
            return new AuthException(ErrorCode401.AUTH_MISSING_CREDENTIALS);
        });
    }
}
