package com.ledgerline.payments.repo;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class PayoutRepository {

    private final JdbcTemplate jdbc;

    public PayoutRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public long insert(String holderName, String ibanSealed, String bankCode,
                       String verificationUrl, String notifyUrl) {
        jdbc.update(
                "INSERT INTO payout_accounts (holder_name, iban_sealed, bank_code, verification_url, notify_url, status) "
                        + "VALUES (?, ?, ?, ?, ?, 'PENDING')",
                holderName, ibanSealed, bankCode, verificationUrl, notifyUrl);
        return jdbc.queryForObject(
                "SELECT MAX(id) FROM payout_accounts WHERE iban_sealed = ?", Long.class, ibanSealed);
    }

    public String findVerificationUrl(long id) {
        return jdbc.queryForObject("SELECT verification_url FROM payout_accounts WHERE id = ?", String.class, id);
    }

    public String findNotifyUrl(long id) {
        return jdbc.queryForObject("SELECT notify_url FROM payout_accounts WHERE id = ?", String.class, id);
    }

    public void markVerified(long id) {
        jdbc.update("UPDATE payout_accounts SET status = 'VERIFIED' WHERE id = ?", id);
    }
}
