package com.ledgerline.payments.repo;

import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentRepository {

    private final JdbcTemplate jdbc;

    public PaymentRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long insert(long linkId, long invoiceId, long amountCents, String method) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO payments (link_id, invoice_id, amount_cents, method, status) "
                            + "VALUES (?, ?, ?, ?, 'CAPTURED')",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setLong(1, linkId);
            ps.setLong(2, invoiceId);
            ps.setLong(3, amountCents);
            ps.setString(4, method);
            return ps;
        }, keys);
        return keys.getKey().longValue();
    }

    public void applyToInvoice(long invoiceId, long amountCents) {
        jdbc.update(
                "UPDATE invoices SET paid_cents = paid_cents + ?, "
                        + "status = CASE WHEN paid_cents + ? >= total_cents THEN 'PAID' ELSE 'PARTIAL' END "
                        + "WHERE id = ?",
                amountCents, amountCents, invoiceId);
    }

    public void recordPayerNote(long paymentId, String note) {
        jdbc.update("INSERT INTO payment_notes (payment_id, note) VALUES (?, ?)", paymentId, note);
    }
}
