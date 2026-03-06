package org.financial.domain;

public record Transaction(String transactionId, String accountId, double amount, String currency ) {
}