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

    // TODO: 조회 수(Hits)를 활용해 동적으로 TTL 증가 로직 구현
    public boolean isMessageIdCached(String eventId) {
        String cacheKey = generateCacheKey(eventId);

        if (localCache.containskey(cacheKey)) {
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

    private void incrementHitsCount(String eventId) {
        String hitsKey = HITS_COUNT_PREFIX + eventId;
        Long hits = redisTemplate.opsForValue().increment(hitsKey);

        if (hits != null && hits % 10 == 0) {
            String cacheKey = generateCacheKey(eventId);
            Long currentTTL = redisTemplate.getExpire(cacheKey, TimeUnit.SECONDS);
            if (currentTTL != null && currentTTL < MAX_TTL) {
                long newTTL = Math.min(currentTTL + 3600, MAX_TTL);
                redisTemplate.expire(cacheKey, newTTL, TimeUnit.SECONDS);
            }
        }
    }

}
