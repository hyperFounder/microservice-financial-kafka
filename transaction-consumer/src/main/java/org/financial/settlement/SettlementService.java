package org.financial.settlement;


import org.financial.domain.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SettlementService {
    private static final Logger log = LoggerFactory.getLogger(SettlementService.class);

    public void settle(Transaction t) {
        log.info("Settling transaction {} for amount {}", t.transactionId(), t.amount());
    }
}