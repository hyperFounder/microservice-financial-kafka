package org.financial.messaging;

import org.financial.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TransactionListener {

    private static final Logger log = LoggerFactory.getLogger(TransactionListener.class);
    private final Set<String> processedTransactions = ConcurrentHashMap.newKeySet();

    @RetryableTopic(
            attempts = "3",
            backoff = @Backoff(delay = 2000, multiplier = 2.0),
            autoCreateTopics = "true"
    )
    @KafkaListener(topics = "transactions", groupId = "transactions-group")
    public void processTransaction(Transaction t){

        log.info("Attempting to process transaction: {}", t.transactionId());
        if (processedTransactions.contains(t.transactionId())){
            log.warn("Duplicated transaction detected [{}]. Ignoring. ", t.transactionId());
            return;
        }
        if (t.amount() <=0){
            log.error("Invalid amount[{}]. Throwing exception to trigger retry/DLQ protocol", t.amount());
            throw new IllegalArgumentException("Transaction amount must be positive.");
        }

        processedTransactions.add(t.transactionId());
        log.info("Successfuly settled transaction [{}] for acount {}.",
                t.transactionId(), t.accountId());
    }

    @DltHandler
    public void handleDeadLetter(Transaction transaction,
                                 @Header(KafkaHeaders.EXCEPTION_MESSAGE) String exceptionMessage){
        log.error("Critical: Transaction {} exhausted all retries and is routed to the DLQ queue. Reason: {}",
                transaction.transactionId(), exceptionMessage);
    }
}