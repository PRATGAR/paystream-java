package com.ledgerline.payments.repo;

final class SqlFragments {

    private static final String INVOICE_REPORT =
            "SELECT id, number, status, total_cents, paid_cents, currency FROM invoices WHERE ";

    private SqlFragments() {
    }

    static String invoiceReport(String whereClause) {
        return INVOICE_REPORT + whereClause + " ORDER BY id";
    }
}
