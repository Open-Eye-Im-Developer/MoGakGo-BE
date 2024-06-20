package io.oeid.mogakgo.domain.log.application;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheService {

    private static final String CACHE_KEY_PREFIX = "processed:";

    private final RedisTemplate<String, Object> redisTemplate;

    public void cacheMessageId(String eventId) {
        String cacheKey = generateCacheKey(eventId);
        redisTemplate.opsForValue().set(cacheKey, true, 1, TimeUnit.DAYS);
    }

    public boolean isMessageIdCached(String eventId) {
        String cacheKey = generateCacheKey(eventId);
        return Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey));
    }

    public String generateCacheKey(String eventId) {
        return CACHE_KEY_PREFIX + eventId;
    }

}
