package com.ledgerline.payments.dto;

public record ImportSummary(long importId, int lineCount, long totalCents, String summaryFile) {
}
