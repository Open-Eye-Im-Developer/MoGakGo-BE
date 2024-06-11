package io.oeid.mogakgo.domain.outbox.domain.entity;

import io.oeid.mogakgo.domain.outbox.domain.EventStatus;
import io.oeid.mogakgo.domain.outbox.domain.EventType;
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

    @Builder
    private OutboxEvent(EventType type, String key) {
        this.type = type;
        this.status = EventStatus.PENDING;
        this.key = key;
    }

}
