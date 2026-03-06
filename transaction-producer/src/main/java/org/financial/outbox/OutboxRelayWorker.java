package org.financial.outbox;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OutboxRelayWorker {

    private static final Logger log = LoggerFactory.getLogger(OutboxRelayWorker.class);
    private final OutboxEventRepository outboxEventRepository;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public OutboxRelayWorker(OutboxEventRepository outboxEventRepository,
                             KafkaTemplate<String, String> kafkaTemplate) {
        this.outboxEventRepository = outboxEventRepository;
        this.kafkaTemplate = kafkaTemplate;
    }

//    @Scheduled(fixedDelayString = "5000"). Comment this to check status as PENDING in database.
    @Scheduled(fixedDelayString = "5000")
    public void pollAndPublishOutbox() {
        List<OutboxEventEntity> pendingEvents = outboxEventRepository.findByStatus("PENDING");

        if (pendingEvents.isEmpty()) {
            return;
        }

        log.info("Discovered {} pending outbox events. Initiating Kafka relay.", pendingEvents.size());

        for (OutboxEventEntity event : pendingEvents) {
            try {
                // Using .get() enforces synchronous publication to guarantee strict ordering.
                // If the broker is unreachable, this throws an exception and halts the loop.
                kafkaTemplate.send(event.getTopic(), event.getAggregateId(), event.getPayload()).get();

                event.setStatus("PUBLISHED");
                outboxEventRepository.save(event);

                log.debug("Successfully relayed outbox event [{}] to Kafka.", event.getEventId());
            } catch (Exception e) {
                log.error("Kafka delivery failure for event [{}]. Will retry on next polling cycle.", event.getEventId(), e);
                // We break the loop to maintain strict FIFO ordering for subsequent messages
                break;
            }
        }
    }
}