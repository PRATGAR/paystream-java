package com.ledgerline.payments.dto;

import java.util.List;
import java.util.Map;

public record ReportRow(List<Map<String, Object>> rows, int count) {

    public static ReportRow of(List<Map<String, Object>> rows) {
        return new ReportRow(rows, rows.size());
    }
}
