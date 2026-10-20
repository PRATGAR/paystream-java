package com.ledgerline.payments.dto;

public record PayoutAccountView(long id, String holderName, String maskedIban, String bankCode, String status) {
}
