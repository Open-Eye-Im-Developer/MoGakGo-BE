package io.oeid.mogakgo.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.oeid.mogakgo.core.properties.event.vo.AchievementEvent;
import io.oeid.mogakgo.core.properties.event.vo.NotificationEvent;
import io.oeid.mogakgo.domain.event.vo.EventType;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Event<T> {

    private String id;
    private EventType eventType;
    private LocalDateTime eventCreatedAt;
    private T event;

    @JsonCreator
    public Event(@JsonProperty("id") String id,
                 @JsonProperty("eventType") EventType eventType,
                 @JsonProperty("eventCreatedAt") LocalDateTime eventCreatedAt,
                 @JsonProperty("event") T event) {
        this.id = id;
        this.eventType = eventType;
        this.eventCreatedAt = eventCreatedAt;
        this.event = event;
    }

    @Builder
    private Event(String id, T event) {
        this.id = id;
        this.eventType = setEventType(event);
        this.eventCreatedAt = LocalDateTime.now();
        this.event = event;
    }

    @Override
    public String toString() {
        return "Event{" +
            "id=" + id +
            ", eventCreatedAt=" + eventCreatedAt +
            ", event=" + event +
            '}';
    }

    private EventType setEventType(T event) {
        if (event instanceof AchievementEvent) {
            return EventType.ACHIEVEMENT;
        } else if (event instanceof NotificationEvent) {
            return EventType.NOTIFICATION;
        } else {
            throw new IllegalArgumentException("Unknown Event:" + event);
        }
    }
}
