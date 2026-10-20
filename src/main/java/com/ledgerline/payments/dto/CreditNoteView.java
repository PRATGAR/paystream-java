package com.ledgerline.payments.dto;

public record CreditNoteView(long id, String number, long invoiceId, long amountCents, String category) {
}
