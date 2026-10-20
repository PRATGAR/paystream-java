package com.ledgerline.payments.web;

import com.ledgerline.payments.bank.BankApiClient;
import com.ledgerline.payments.dto.BankModels.BankStatement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/bank")
public class BankController {

    private final BankApiClient bank;

    public BankController(BankApiClient bank) {
        this.bank = bank;
    }

    @GetMapping("/statements")
    public BankStatement bankStatement(@RequestParam String accountRef) {
        return bank.fetchStatement(accountRef);
    }
}
