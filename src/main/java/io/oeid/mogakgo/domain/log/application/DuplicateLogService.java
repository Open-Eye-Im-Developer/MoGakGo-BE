package io.oeid.mogakgo.domain.log.application;

import io.oeid.mogakgo.domain.log.domain.entity.MessageLog;
import io.oeid.mogakgo.domain.log.infrastructure.MessageLogJpaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class DuplicateLogService {

    private final MessageLogJpaRepository messageLogRepository;
    private final CacheService cacheService;

    public boolean isDuplicate(String messageId) {
        return messageLogRepository.existsByMessageId(messageId);
    }

    public void delete(String messageId) {
        messageLogRepository.deletebyMessageId(messageId);
    }

    public MessageLog caching(String messageId) {
        log.info("received message with messageId '{}' processing and caching complete!", messageId);
        MessageLog messageLog = generate(messageId);
        MessageLog savedLog = messageLogRepository.save(messageLog);
        cacheService.cacheMessageId(savedLog.getMessageId());
        return savedLog;
    }

    public boolean isMessageIdProcessed(String messageId) {
        return cacheService.isMessageIdCached(messageId);
    }

    public MessageLog generate(String messageId) {
        return MessageLog.builder().messageId(messageId).build();
    }
}
