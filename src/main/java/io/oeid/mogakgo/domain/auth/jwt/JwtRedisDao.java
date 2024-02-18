package io.oeid.mogakgo.domain.auth.jwt;

import io.oeid.mogakgo.core.properties.JwtProperties;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional(readOnly = true)
    // TODO: 2024-02-15 if null then what should I do?
    public String getRefreshTokenByAccessToken(String accessToken) {
        var result = Optional.ofNullable(redisTemplate.opsForValue().get(accessToken));
        return result.orElseThrow();
    }
}
