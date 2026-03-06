package org.financial.infrastructure.outbox;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "outbox_events")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OutboxEventEntity {

    @Id
    private String eventId;
    private String aggregateId;
    private String topic;
    @Column(columnDefinition = "TEXT")
    private String payload;
    private String status;

}