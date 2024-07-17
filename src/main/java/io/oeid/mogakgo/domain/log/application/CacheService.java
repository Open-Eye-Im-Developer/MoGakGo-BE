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
    private static final String HITS_COUNT_PREFIX = "hits:";
    private static final Long INIT_TTL = 3600L;
    private static final Long MAX_TTL = 86400L;

    private final RedisTemplate<String, Object> redisTemplate;
    private final LocalCache localCache;

    public void cacheMessageId(String eventId) {
        String cacheKey = generateCacheKey(eventId);
        redisTemplate.opsForValue().set(cacheKey, true, INIT_TTL, TimeUnit.SECONDS);
        localCache.put(cacheKey, "processed");
    }

    public boolean isMessageIdCached(String eventId) {
        String cacheKey = generateCacheKey(eventId);

        if (localCache.containskey(cacheKey)) {
            log.info("local cache founded!");
            incrementHitsCount(eventId);
            return true;
        }

        if (Boolean.TRUE.equals(redisTemplate.hasKey(cacheKey))) {
            localCache.put(cacheKey, "processed");
            incrementHitsCount(eventId);
            return true;
        }
        return false;
    }

    public String generateCacheKey(String eventId) {
        return CACHE_KEY_PREFIX + eventId;
    }

    public void incrementHitsCount(String eventId) {
        String hitsKey = HITS_COUNT_PREFIX + eventId;
        Long hits = redisTemplate.opsForValue().increment(hitsKey, 1);

        if (hits != null && hits % 10 != 0) {
            String cacheKey = generateCacheKey(eventId);
            Long currentTTL = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
            if (currentTTL != null && currentTTL < MAX_TTL) {
                long newTTL = Math.min(currentTTL + 3600, MAX_TTL);
                redisTemplate.expire(cacheKey, newTTL, TimeUnit.SECONDS);
                log.info("cache for eventId '{}' TTL is updated now! currrentTTL: '{}', updated TTL: '{}'",
                    eventId, currentTTL, newTTL);
            }
        }
    }

}
