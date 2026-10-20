package com.ledgerline.payments.dto;

public record StatementLine(String reference, long amountCents, String description) {
}
