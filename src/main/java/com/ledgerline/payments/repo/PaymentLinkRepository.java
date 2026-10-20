package com.ledgerline.payments.repo;

import com.ledgerline.payments.dto.PaymentLink;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentLinkRepository {

    private static final RowMapper<PaymentLink> MAPPER = (rs, rowNum) -> new PaymentLink(
            rs.getLong("id"),
            rs.getLong("invoice_id"),
            rs.getString("token"),
            rs.getLong("amount_cents"),
            rs.getString("reference"),
            rs.getString("return_url"),
            rs.getString("callback_url"),
            rs.getString("status"));

    private final JdbcTemplate jdbc;

    public PaymentLinkRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public void insert(long invoiceId, String token, long amountCents, String reference,
                       String returnUrl, String callbackUrl) {
        jdbc.update(
                "INSERT INTO payment_links (invoice_id, token, amount_cents, reference, return_url, "
                        + "callback_url, status) VALUES (?, ?, ?, ?, ?, ?, 'OPEN')",
                invoiceId, token, amountCents, reference, returnUrl, callbackUrl);
    }

    public PaymentLink findByToken(String token) {
        List<PaymentLink> rows = jdbc.query("SELECT * FROM payment_links WHERE token = ?", MAPPER, token);
        return rows.isEmpty() ? null : rows.get(0);
    }

    public void markPaid(long linkId) {
        jdbc.update("UPDATE payment_links SET status = 'PAID' WHERE id = ?", linkId);
    }

    public void markCancelled(String token) {
        jdbc.update("UPDATE payment_links SET status = 'CANCELLED' WHERE token = ? AND status = 'OPEN'", token);
    }
}
