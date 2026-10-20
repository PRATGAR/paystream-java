package com.ledgerline.payments.repo;

import com.ledgerline.payments.dto.CreateInvoiceRequest;
import com.ledgerline.payments.dto.InvoiceRow;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

@Repository
public class InvoiceRepository {

    private static final String COLUMNS =
            "i.id, i.number, i.customer_id, i.status, i.total_cents, i.paid_cents, i.currency, i.due_date";

    private static final RowMapper<InvoiceRow> MAPPER = (rs, rowNum) -> new InvoiceRow(
            rs.getLong("id"),
            rs.getString("number"),
            rs.getLong("customer_id"),
            rs.getString("status"),
            rs.getLong("total_cents"),
            rs.getLong("paid_cents"),
            rs.getString("currency"),
            rs.getObject("due_date", LocalDate.class));

    private final JdbcTemplate jdbc;

    public InvoiceRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public String nextNumber(String series) {
        return jdbc.queryForObject(
                "SELECT '" + series + "-' || CAST(NEXT VALUE FOR invoice_seq AS VARCHAR)", String.class);
    }

    public long insert(String number, long customerId, long totalCents, String currency, LocalDate dueDate) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO invoices (number, customer_id, status, total_cents, currency, due_date) "
                            + "VALUES (?, ?, 'OPEN', ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, number);
            ps.setLong(2, customerId);
            ps.setLong(3, totalCents);
            ps.setString(4, currency);
            ps.setObject(5, dueDate);
            return ps;
        }, keys);
        return keys.getKey().longValue();
    }

    public void insertLines(long invoiceId, List<CreateInvoiceRequest.LineItem> lines) {
        jdbc.batchUpdate(
                "INSERT INTO invoice_lines (invoice_id, description, quantity, unit_cents) VALUES (?, ?, ?, ?)",
                lines,
                lines.size(),
                (ps, line) -> {
                    ps.setLong(1, invoiceId);
                    ps.setString(2, line.description());
                    ps.setInt(3, line.quantity());
                    ps.setLong(4, line.unitCents());
                });
    }

    public InvoiceRow findByNumber(String number) {
        return jdbc.queryForObject(
                "SELECT " + COLUMNS + " FROM invoices i WHERE i.number = ?", MAPPER, number);
    }

    public InvoiceRow findById(long id) {
        return jdbc.queryForObject("SELECT " + COLUMNS + " FROM invoices i WHERE i.id = " + id, MAPPER);
    }

    public List<InvoiceRow> findByCustomerName(String name) {
        return jdbc.query(
                "SELECT " + COLUMNS + " FROM invoices i JOIN customers c ON c.id = i.customer_id "
                        + "WHERE c.name LIKE ? ORDER BY i.issued_at DESC",
                MAPPER,
                "%" + SqlText.likeTerm(name) + "%");
    }

    public int countByStatus(String status) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM invoices WHERE status = '" + status + "'", Integer.class);
        return count == null ? 0 : count;
    }

    public List<InvoiceRow> findRecent(int limit) {
        return jdbc.query(
                "SELECT " + COLUMNS + " FROM invoices i ORDER BY i.issued_at DESC LIMIT " + limit, MAPPER);
    }

    public long outstandingCents(long invoiceId) {
        Long outstanding = jdbc.queryForObject(
                "SELECT total_cents - paid_cents FROM invoices WHERE id = " + invoiceId, Long.class);
        return outstanding == null ? 0L : outstanding;
    }

    public void insertNote(long invoiceId, String note) {
        jdbc.update("INSERT INTO invoice_notes (invoice_id, note) VALUES (?, ?)", invoiceId, note);
    }

    public void markCancelled(long invoiceId, String reason) {
        jdbc.update(
                "UPDATE invoices SET status = 'CANCELLED', cancel_reason = ? WHERE id = ?",
                reason,
                invoiceId);
    }
}
