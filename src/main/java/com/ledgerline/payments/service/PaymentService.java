package com.ledgerline.payments.service;

import com.ledgerline.payments.dto.PaymentLink;
import com.ledgerline.payments.dto.PaymentReceipt;
import com.ledgerline.payments.dto.SubmitPaymentRequest;
import com.ledgerline.payments.repo.AuditRepository;
import com.ledgerline.payments.repo.PaymentLinkRepository;
import com.ledgerline.payments.repo.PaymentRepository;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentLinkRepository links;
    private final PaymentRepository payments;
    private final AuditRepository audit;
    private final WebhookDispatcher webhooks;

    public PaymentService(PaymentLinkRepository links, PaymentRepository payments,
                          AuditRepository audit, WebhookDispatcher webhooks) {
        this.links = links;
        this.payments = payments;
        this.audit = audit;
        this.webhooks = webhooks;
    }

    @Transactional
    public PaymentReceipt recordPayment(String token, SubmitPaymentRequest request) {
        PaymentLink link = links.findByToken(token);
        if (link == null) {
            throw new NotFoundException("payment link not found");
        }
        if (!"OPEN".equals(link.status())) {
            throw new IllegalStateException("payment link is no longer open");
        }
        long paymentId = payments.insert(link.id(), link.invoiceId(), request.amountCents(), request.method());
        payments.applyToInvoice(link.invoiceId(), request.amountCents());
        if (request.payerNote() != null && !request.payerNote().isBlank()) {
            payments.recordPayerNote(paymentId, request.payerNote());
        }
        audit.append(AuditEvents.PAYMENT_RECEIVED, link.reference());
        links.markPaid(link.id());
        if (link.callbackUrl() != null && !link.callbackUrl().isBlank()) {
            webhooks.send(link.callbackUrl(), Map.of(
                    "event", "payment.received",
                    "paymentId", paymentId,
                    "reference", link.reference()));
        }
        return new PaymentReceipt(paymentId, "CAPTURED", request.amountCents(), link.returnUrl());
    }

    public void cancelLink(String token) {
        links.markCancelled(token);
    }
}
