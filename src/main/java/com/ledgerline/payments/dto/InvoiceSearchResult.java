package com.ledgerline.payments.dto;

import java.util.List;

public record InvoiceSearchResult(List<InvoiceRow> matches, String status, int statusCount) {
}
