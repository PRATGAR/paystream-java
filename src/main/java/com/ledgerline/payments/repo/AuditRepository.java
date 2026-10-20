package com.ledgerline.payments.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AuditRepository {

    private final JdbcTemplate jdbc;

    public AuditRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void append(String event, String detail) {
        jdbc.update("INSERT INTO audit_log (event, detail) VALUES (?, ?)", event, detail);
    }

    public int countForEvent(String event) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM audit_log WHERE event = ?", Integer.class, event);
        return count == null ? 0 : count;
    }
}
