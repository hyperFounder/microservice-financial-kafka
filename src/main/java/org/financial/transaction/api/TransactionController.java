package org.financial.transaction.api;

// Ensure this import points to the correct new package
import org.financial.transaction.service.TransactionCommandService;
import org.financial.transaction.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private static final Logger log = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionCommandService transactionCommandService;

    public TransactionController(TransactionCommandService transactionCommandService) {
        this.transactionCommandService = transactionCommandService;
    }

    @PostMapping
    public String submitTransaction(@RequestParam String accountId, @RequestParam double amount) {
        Transaction transaction = new Transaction(UUID.randomUUID().toString(), accountId, amount, "GBP");

        log.info("Received HTTP request for new transaction [{}]", transaction.transactionId());
        transactionCommandService.processAndSave(transaction);

        return "Transaction accepted safely for processing: " + transaction.transactionId();
    }
}
