package io.oeid.mogakgo.domain.outbox.domain.entity;

import static io.oeid.mogakgo.exception.code.ErrorCode400.EVENT_ALREADY_COMPLETED;

import io.oeid.mogakgo.domain.outbox.domain.EventStatus;
import io.oeid.mogakgo.domain.outbox.domain.EventType;
import io.oeid.mogakgo.domain.outbox.exception.OutboxException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Entity
@Table(name = "outbox_tb")
@NoArgsConstructor
public class OutboxEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "event_type")
    private EventType type;

    @Column(name = "event_status")
    private EventStatus status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "key")
    private String key;

    @Column(name = "target")
    private Integer target;

    @Builder
    private OutboxEvent(EventType type, String key, Integer target) {
        this.type = type;
        this.status = EventStatus.PENDING;
        this.key = key;
        this.target = target;
    }

    public void complete() {
        validateAvailableComplete();
        this.status = EventStatus.COMPLETED;
    }

    private void validateAvailableComplete() {
        if (this.status == EventStatus.COMPLETED) {
            throw new OutboxException(EVENT_ALREADY_COMPLETED);
        }
    }

}
