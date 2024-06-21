package io.oeid.mogakgo.domain.log.application;

import io.oeid.mogakgo.domain.log.domain.entity.MessageLog;
import io.oeid.mogakgo.domain.log.infrastructure.MessageLogJpaRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheWarmingService {

    private final MessageLogJpaRepository messageLogRepository;
    private final CacheService cacheService;

    @PostConstruct
    public void warnUpCache() {
        List<MessageLog> logList = messageLogRepository.findAll();

        for (MessageLog log : logList) {
            String eventId = log.getEventId();
            cacheService.cacheMessageId(eventId);
        }
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void scheduleCacheWarming() {
        warnUpCache();
    }

}
