package org.financial.transaction.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.financial.infrastructure.outbox.OutboxEventEntity;
import org.financial.infrastructure.outbox.OutboxEventRepository;
import org.financial.transaction.domain.Transaction;
import org.financial.transaction.domain.TransactionEntity;
import org.financial.transaction.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;


@Service
public class TransactionCommandService {

    private static final Logger log = LoggerFactory.getLogger(TransactionCommandService.class);

    private final TransactionRepository transactionRepository;
    private final OutboxEventRepository outboxEventRepository;
    private final ObjectMapper objectMapper;

    public TransactionCommandService(TransactionRepository transactionRepository,
                                     OutboxEventRepository outboxEventRepository,
                                     ObjectMapper objectMapper) {
        this.transactionRepository = transactionRepository;
        this.outboxEventRepository = outboxEventRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void processAndSave(Transaction transaction){
        log.info("Initiating local database transaction for [{}]", transaction.transactionId());

        TransactionEntity entity = new TransactionEntity(
                transaction.transactionId(),
                transaction.accountId(),
                transaction.amount(),
                transaction.currency()
        );

        // Persist the mapped entity
        transactionRepository.save(entity);

        try {
            String payload = objectMapper.writeValueAsString(transaction);
            OutboxEventEntity outboxEvent = new OutboxEventEntity(
                    UUID.randomUUID().toString(),
                    transaction.accountId(),
                    "trasactions",
                    payload,
                    "PENDING"
            );
            outboxEventRepository.save(outboxEvent);
            log.info("Successfully committed dual-write for transaction [{}]", transaction.transactionId());
        } catch (JsonProcessingException e){
            log.error("Serialization failed. Rolling back transaction.", e);
            throw new IllegalArgumentException("Interal serialization error", e);
        }
    }
}