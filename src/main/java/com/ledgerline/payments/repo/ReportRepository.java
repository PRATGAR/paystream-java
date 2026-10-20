package com.ledgerline.payments.repo;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportRepository {

    private final JdbcTemplate jdbc;

    public ReportRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long insertPreset(String name, String whereClause) {
        jdbc.update("INSERT INTO report_presets (name, where_clause) VALUES (?, ?)", name, whereClause);
        return jdbc.queryForObject("SELECT MAX(id) FROM report_presets WHERE name = ?", Long.class, name);
    }

    public String findPresetWhere(long id) {
        return jdbc.queryForObject("SELECT where_clause FROM report_presets WHERE id = ?", String.class, id);
    }

    public List<Map<String, Object>> runPresetReport(String whereClause) {
        return jdbc.queryForList(SqlFragments.invoiceReport(whereClause));
    }

    public long insertFilter(String name, String whereClause) {
        jdbc.update("INSERT INTO saved_filters (name, where_clause) VALUES (?, ?)", name, whereClause);
        return jdbc.queryForObject("SELECT MAX(id) FROM saved_filters WHERE name = ?", Long.class, name);
    }

    public String findFilterWhere(long id) {
        return jdbc.queryForObject("SELECT where_clause FROM saved_filters WHERE id = ?", String.class, id);
    }

    public List<Map<String, Object>> runFilterReport(String whereClause) {
        return jdbc.queryForList(
                "SELECT id, number, status, total_cents, paid_cents, currency FROM invoices WHERE "
                        + whereClause + " ORDER BY id");
    }
}
