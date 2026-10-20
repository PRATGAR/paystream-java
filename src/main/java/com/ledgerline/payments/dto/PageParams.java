package com.ledgerline.payments.dto;

import jakarta.validation.constraints.Min;

public record PageParams(@Min(1) Integer limit) {

    public int limitOrDefault() {
        return limit == null ? 20 : limit;
    }
}
