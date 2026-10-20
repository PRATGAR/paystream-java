package com.ledgerline.payments.service;

import com.ledgerline.payments.dto.CreatePaymentLinkRequest;
import com.ledgerline.payments.dto.PaymentLinkView;
import com.ledgerline.payments.repo.InvoiceRepository;
import com.ledgerline.payments.repo.PaymentLinkRepository;
import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class PaymentLinkService {

    private static final SecureRandom RANDOM = new SecureRandom();

    private final InvoiceRepository invoices;
    private final PaymentLinkRepository links;

    public PaymentLinkService(InvoiceRepository invoices, PaymentLinkRepository links) {
        this.invoices = invoices;
        this.links = links;
    }

    public PaymentLinkView create(CreatePaymentLinkRequest request) {
        long outstanding = invoices.outstandingCents(request.invoiceId());
        if (request.amountCents() > outstanding) {
            throw new IllegalArgumentException("link amount exceeds the outstanding balance");
        }
        String token = newToken();
        links.insert(request.invoiceId(), token, request.amountCents(), request.reference(),
                request.returnUrl(), request.callbackUrl());
        return new PaymentLinkView(token, "/pay/" + token, request.amountCents(), request.reference());
    }

    private static String newToken() {
        byte[] raw = new byte[24];
        RANDOM.nextBytes(raw);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(raw);
    }
}
