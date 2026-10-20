package com.ledgerline.payments.web;

import com.ledgerline.payments.dto.CreatePaymentLinkRequest;
import com.ledgerline.payments.dto.PaymentLinkView;
import com.ledgerline.payments.service.PaymentLinkService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment-links")
public class PaymentLinkController {

    private final PaymentLinkService linkService;

    public PaymentLinkController(PaymentLinkService linkService) {
        this.linkService = linkService;
    }

    @PostMapping
    public ResponseEntity<PaymentLinkView> createLink(@Valid @RequestBody CreatePaymentLinkRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(linkService.create(request));
    }
}
