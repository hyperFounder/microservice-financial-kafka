package org.financial.producer;

import org.financial.consumer.TransactionProcessor;

import org.financial.domain.Transaction;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionGateway {

    private static final Logger log = LoggerFactory.getLogger(TransactionProcessor.class);
    private final KafkaTemplate<String, Transaction> template;
    private static final String TOPIC = "transactions";

    public TransactionGateway(KafkaTemplate<String, Transaction> kafkaTemplate){
        this.template = kafkaTemplate;
    }

    @PostMapping
    public String submitTransaction(@RequestParam String accountId, @RequestParam double amount){
        Transaction T = new Transaction(UUID.randomUUID().toString(), accountId, amount, "GBP");
        template.send(TOPIC, accountId, T);

        log.info("Published transaction [{}] to Kafka for account [{}]",
                T.transactionId(), accountId);

        return "Transaction accepted for processing " + T.transactionId();

    }

}
