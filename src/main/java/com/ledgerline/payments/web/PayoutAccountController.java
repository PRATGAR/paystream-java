package com.ledgerline.payments.web;

import com.ledgerline.payments.dto.CreatePayoutAccountRequest;
import com.ledgerline.payments.dto.PayoutAccountView;
import com.ledgerline.payments.service.PayoutService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payout-accounts")
public class PayoutAccountController {

    private final PayoutService payouts;

    public PayoutAccountController(PayoutService payouts) {
        this.payouts = payouts;
    }

    @PostMapping
    public ResponseEntity<PayoutAccountView> createPayoutAccount(
            @Valid @RequestBody CreatePayoutAccountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(payouts.create(request));
    }

    @PostMapping("/{id}/verify")
    public Map<String, Boolean> verifyPayoutAccount(@PathVariable long id) {
        return Map.of("verified", payouts.verify(id));
    }

    @PostMapping("/{id}/notify-test")
    public ResponseEntity<Void> sendTestNotification(@PathVariable long id) {
        payouts.sendTestNotification(id);
        return ResponseEntity.accepted().build();
    }
}
