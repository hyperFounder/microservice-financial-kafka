package org.financial.producer;

import org.financial.domain.Transaction;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(TransactionGateway.class)
public class TransactionGatewayTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private KafkaTemplate<String, Transaction> kafkaTemplate;

    @Test
    /**
     * Should submit to Kafka and return a success
     */
    public void submitTransaction() throws Exception {
        String accountId = "ACC-123";
        double amount = 500;

        // Act
        mockMvc.perform(post("/api/transactions")
                        .param("accountId", accountId)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk());

        // Assert
        ArgumentCaptor<Transaction> argumentCaptor = ArgumentCaptor.forClass(Transaction.class);

        // Verify the template was called exactly once with the correct TOPIC (transactions) and key (account ID)
        verify(kafkaTemplate).send(eq("transactions"), eq(accountId), argumentCaptor.capture());

        // Verify the payload contents
        Transaction capturedTransaction = argumentCaptor.getValue();
        assertEquals(accountId, capturedTransaction.accountId());
        assertEquals(amount, capturedTransaction.amount());
        assertEquals("GBP", capturedTransaction.currency());
    }
}
