package io.oeid.mogakgo.core.properties.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.oeid.mogakgo.domain.event.Event;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class MessageConsumer {

    private final ObjectMapper objectMapper;
    private List<Event> eventRepo = new ArrayList<>();

    @KafkaListener(topics = "my-topic", groupId = "my-group", containerFactory = "kafkaListenerContainerFactory")
    protected void consume(@Payload String payload) throws Exception {
        log.info("receive event: {}", payload);
        Event event = objectMapper.readValue(payload, Event.class);
        eventRepo.add(event);

        // Process
        // acknowledgment.acknowledge();
    }

    public List<Event> getEventRepo() { return eventRepo; }

}
