package com.ledgerline.payments.repo;

import com.ledgerline.payments.dto.CreditNoteView;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class CreditNoteRepository {

    @PersistenceContext
    private EntityManager em;

    private final JdbcTemplate jdbc;

    public CreditNoteRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public int countForCustomerRef(String customerRef) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM credit_notes WHERE customer_ref = ?", Integer.class, customerRef);
        return count == null ? 0 : count;
    }

    public long insert(String number, long invoiceId, long amountCents, String reason,
                       String category, String customerRef) {
        KeyHolder keys = new GeneratedKeyHolder();
        jdbc.update(con -> {
            PreparedStatement ps = con.prepareStatement(
                    "INSERT INTO credit_notes (number, invoice_id, amount_cents, reason, category, customer_ref) "
                            + "VALUES (?, ?, ?, ?, ?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, number);
            ps.setLong(2, invoiceId);
            ps.setLong(3, amountCents);
            ps.setString(4, reason);
            ps.setString(5, category);
            ps.setString(6, customerRef);
            return ps;
        }, keys);
        return keys.getKey().longValue();
    }

    public String findReason(long id) {
        return jdbc.queryForObject("SELECT reason FROM credit_notes WHERE id = ?", String.class, id);
    }

    public String findCategory(long id) {
        return jdbc.queryForObject("SELECT category FROM credit_notes WHERE id = ?", String.class, id);
    }

    @Transactional(readOnly = true)
    public List<CreditNoteView> findRelated(long id, String reason) {
        List<?> rows = em.createNativeQuery(NativeQueries.creditNotesByReason(id, reason)).getResultList();
        return rows.stream().map(CreditNoteRepository::toView).toList();
    }

    @Transactional(readOnly = true)
    public List<CreditNoteView> findPeers(long id, String category) {
        List<?> rows = em.createNativeQuery(
                "SELECT id, number, invoice_id, amount_cents, category FROM credit_notes WHERE category = '"
                        + category + "' AND id <> " + id + " ORDER BY created_at DESC").getResultList();
        return rows.stream().map(CreditNoteRepository::toView).toList();
    }

    private static CreditNoteView toView(Object row) {
        Object[] cols = (Object[]) row;
        return new CreditNoteView(
                ((Number) cols[0]).longValue(),
                (String) cols[1],
                ((Number) cols[2]).longValue(),
                ((Number) cols[3]).longValue(),
                (String) cols[4]);
    }
}
