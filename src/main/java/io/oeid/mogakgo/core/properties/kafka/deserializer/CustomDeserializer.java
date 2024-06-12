package io.oeid.mogakgo.core.properties.kafka.deserializer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.core.properties.event.vo.NotificationEvent;
import io.oeid.mogakgo.domain.event.Event;
import io.oeid.mogakgo.domain.event.vo.EventType;
import java.io.IOException;
import java.time.LocalDateTime;
import org.apache.kafka.common.serialization.Deserializer;

public class CustomDeserializer implements Deserializer<Event<?>> {

    private final ObjectMapper objectMapper;

    public CustomDeserializer() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public Event<?> deserialize(String topic, byte[] data) {
        try {
            if (data == null) return null;

            JsonNode jsonNode = objectMapper.readTree(data);
            Long id = jsonNode.get("id").asLong();
            EventType eventType = objectMapper
                .treeToValue(jsonNode.get("eventType"), EventType.class);
            LocalDateTime eventCreatedAt = objectMapper
                .treeToValue(jsonNode.get("eventCreatedAt"), LocalDateTime.class);
            JsonNode event = jsonNode.get("event");

            Class<?> dataClass = getDataClassForEventType(eventType);
            Object eventData = objectMapper.treeToValue(event, dataClass);

            return new Event<>(id, eventType, eventCreatedAt, eventData);

        } catch (IOException e) {
            throw new RuntimeException("Failed to process readTree using objectMapper from data", e);
        }
    }

    private Class<?> getDataClassForEventType(EventType eventType) {
        switch (eventType) {
            case ACHIEVEMENT -> {
                return AchievementEvent.class;
            }
            case NOTIFICATION -> {
                return NotificationEvent.class;
            }
            default -> throw new IllegalArgumentException("Unknown EventType: " + eventType);
        }
    }

}
