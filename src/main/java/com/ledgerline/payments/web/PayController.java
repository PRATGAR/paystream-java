package com.ledgerline.payments.web;

import com.ledgerline.payments.dto.PaymentReceipt;
import com.ledgerline.payments.dto.SubmitPaymentRequest;
import com.ledgerline.payments.service.PaymentService;
import com.ledgerline.payments.service.Redirects;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.io.IOException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pay")
public class PayController {

    private final PaymentService payments;

    public PayController(PaymentService payments) {
        this.payments = payments;
    }

    @PostMapping("/{token}/submit")
    public PaymentReceipt submitPayment(@PathVariable String token,
                                        @Valid @RequestBody SubmitPaymentRequest request,
                                        HttpServletResponse response) throws IOException {
        PaymentReceipt receipt = payments.recordPayment(token, request);
        if (receipt.returnUrl() != null && !receipt.returnUrl().isBlank()) {
            response.sendRedirect(receipt.returnUrl());
            return null;
        }
        return receipt;
    }

    @GetMapping("/{token}/cancel")
    public void cancelPayment(@PathVariable String token,
                              @RequestParam(required = false) String next,
                              HttpServletResponse response) throws IOException {
        payments.cancelLink(token);
        response.sendRedirect(Redirects.local(next));
    }
}
