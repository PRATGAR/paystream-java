package com.ledgerline.payments.service;

import com.ledgerline.payments.bank.BankApiClient;
import com.ledgerline.payments.bank.HttpTargets;
import com.ledgerline.payments.dto.BankModels.BankInstitution;
import com.ledgerline.payments.dto.BankModels.ProbeResult;
import com.ledgerline.payments.dto.CreatePayoutAccountRequest;
import com.ledgerline.payments.dto.PayoutAccountView;
import com.ledgerline.payments.repo.PayoutRepository;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PayoutService {

    private final PayoutRepository payouts;
    private final IbanVault vault;
    private final BankApiClient bank;
    private final WebhookDispatcher webhooks;

    public PayoutService(PayoutRepository payouts, IbanVault vault, BankApiClient bank,
                         WebhookDispatcher webhooks) {
        this.payouts = payouts;
        this.vault = vault;
        this.bank = bank;
        this.webhooks = webhooks;
    }

    public PayoutAccountView create(CreatePayoutAccountRequest request) {
        BankInstitution institution = bank.institution(request.bankCode());
        if (institution == null) {
            throw new IllegalArgumentException("unknown bank code");
        }
        String sealed = vault.seal(request.iban());
        long id = payouts.insert(request.holderName(), sealed, request.bankCode(),
                request.verificationUrl(), request.notifyUrl());
        return new PayoutAccountView(id, request.holderName(), mask(request.iban()),
                request.bankCode(), "PENDING");
    }

    public boolean verify(long accountId) {
        String url;
        try {
            url = payouts.findVerificationUrl(accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("payout account " + accountId + " not found");
        }
        ProbeResult result = bank.probe(HttpTargets.toUri(url));
        if (result != null && result.confirmed()) {
            payouts.markVerified(accountId);
            return true;
        }
        return false;
    }

    public void sendTestNotification(long accountId) {
        String url;
        try {
            url = payouts.findNotifyUrl(accountId);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("payout account " + accountId + " not found");
        }
        webhooks.send(url, Map.of("event", "payout.account.test", "accountId", accountId));
    }

    private static String mask(String iban) {
        return iban.substring(0, 4) + "****" + iban.substring(iban.length() - 4);
    }
}
