package com.ledgerline.payments.dto;

import java.util.List;

public final class BankModels {

    private BankModels() {
    }

    public record BankInstitution(String code, String name, String country) {
    }

    public record ProbeResult(boolean confirmed, String detail) {
    }

    public record BankStatement(String accountRef, String currency, List<Entry> entries) {

        public record Entry(String reference, long amountCents, String bookedOn) {
        }
    }
}
