package io.oeid.mogakgo.domain.log.application;

import io.oeid.mogakgo.domain.log.domain.entity.MessageLog;
import io.oeid.mogakgo.domain.log.infrastructure.MessageLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DuplicateLogService {

    private final MessageLogJpaRepository messageLogRepository;
    private final CacheService cacheService;
    private final LocalCache localCache;

    public boolean isDuplicate(String eventId) {
        return messageLogRepository.existsByEventId(eventId);
    }

    public void delete(String eventId) {
        messageLogRepository.deleteByEventId(eventId);
    }

    // TODO: 트랜잭션 처리
    public MessageLog caching(String eventId) {
        log.info("received message with eventId '{}' processing and caching complete!", eventId);
        MessageLog messageLog = generate(eventId);
        MessageLog savedLog = messageLogRepository.save(messageLog);
        cacheService.cacheMessageId(eventId);
        return savedLog;
    }

    public boolean isMessageIdProcessed(String eventId) {
        return cacheService.isMessageIdCached(eventId);
    }

    public MessageLog generate(String eventId) {
        return MessageLog.builder()
            .eventId(eventId)
            .build();
    }
}
