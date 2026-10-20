package com.ledgerline.payments.repo;

import com.ledgerline.payments.dto.StatementLine;
import java.time.LocalDate;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReconciliationRepository {

    private final JdbcTemplate jdbc;

    public ReconciliationRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public boolean existsFingerprint(String fingerprint) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM recon_imports WHERE fingerprint = ?", Integer.class, fingerprint);
        return count != null && count > 0;
    }

    public long insertImport(String fileName, String fingerprint, LocalDate batchDate) {
        jdbc.update(
                "INSERT INTO recon_imports (file_name, fingerprint, batch_date, status) VALUES (?, ?, ?, 'REGISTERED')",
                fileName, fingerprint, batchDate);
        return jdbc.queryForObject(
                "SELECT id FROM recon_imports WHERE fingerprint = ?", Long.class, fingerprint);
    }

    public String findImportFileName(long importId) {
        return jdbc.queryForObject("SELECT file_name FROM recon_imports WHERE id = ?", String.class, importId);
    }

    public void markProcessed(long importId) {
        jdbc.update("UPDATE recon_imports SET status = 'PROCESSED' WHERE id = ?", importId);
    }

    public void insertLines(long importId, List<StatementLine> lines) {
        jdbc.batchUpdate(
                "INSERT INTO recon_lines (import_id, reference, amount_cents, description) VALUES (?, ?, ?, ?)",
                lines,
                lines.size(),
                (ps, line) -> {
                    ps.setLong(1, importId);
                    ps.setString(2, line.reference());
                    ps.setLong(3, line.amountCents());
                    ps.setString(4, line.description());
                });
    }

    public long insertUpload(String originalName, String storedName) {
        jdbc.update("INSERT INTO recon_uploads (original_name, stored_name) VALUES (?, ?)",
                originalName, storedName);
        return jdbc.queryForObject(
                "SELECT MAX(id) FROM recon_uploads WHERE stored_name = ?", Long.class, storedName);
    }

    public String findStoredName(long uploadId) {
        return jdbc.queryForObject("SELECT stored_name FROM recon_uploads WHERE id = ?", String.class, uploadId);
    }
}
