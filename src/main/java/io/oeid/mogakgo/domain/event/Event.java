package io.oeid.mogakgo.domain.event;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class Event {

    private Long id;
    private LocalDateTime eventCreatedAt;

    @JsonCreator
    public Event(@JsonProperty("id") Long id, @JsonProperty("eventCreatedAt") LocalDateTime eventCreatedAt) {
        this.id = id;
        this.eventCreatedAt = eventCreatedAt;
    }

    public static Event idOf(Long id) {
        return new Event(id, LocalDateTime.now());
    }

}
