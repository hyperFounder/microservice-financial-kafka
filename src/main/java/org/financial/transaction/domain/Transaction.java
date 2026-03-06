package org.financial.transaction.domain;

public record Transaction(String transactionId, String accountId, double amount, String currency ) {
}