package io.oeid.mogakgo.domain.log.application;

import io.oeid.mogakgo.domain.log.domain.entity.MessageLog;
import io.oeid.mogakgo.domain.log.infrastructure.MessageLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional("transactionManager")
@RequiredArgsConstructor
public class DuplicateLogService {

    private final MessageLogJpaRepository messageLogRepository;
    private final CacheService cacheService;

    public boolean isDuplicate(String eventId) {
        return messageLogRepository.existsByEventId(eventId);
    }

    public void delete(String eventId) {
        messageLogRepository.deleteByEventId(eventId);
    }

    public MessageLog caching(String eventId) {
        log.info("received message with eventId '{}' processing and caching complete!", eventId);
        MessageLog messageLog = generate(eventId);
        // cacheService.cacheMessageId(eventId);
        return messageLogRepository.save(messageLog);
    }

    public boolean isMessageIdProcessed(String eventId) {
        //return cacheService.isMessageIdCached(eventId);
        return isDuplicate(eventId);
    }

    public MessageLog generate(String eventId) {
        return MessageLog.builder()
            .eventId(eventId)
            .build();
    }
}
