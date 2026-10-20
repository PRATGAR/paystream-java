package com.ledgerline.payments.bank;

import com.ledgerline.payments.config.AppProperties;
import com.ledgerline.payments.dto.BankModels.BankInstitution;
import com.ledgerline.payments.dto.BankModels.BankStatement;
import com.ledgerline.payments.dto.BankModels.ProbeResult;
import java.net.URI;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

@Component
public class BankApiClient {

    private final RestClient bankRestClient;
    private final RestTemplate restTemplate;
    private final AppProperties props;

    public BankApiClient(RestClient bankRestClient, RestTemplate restTemplate, AppProperties props) {
        this.bankRestClient = bankRestClient;
        this.restTemplate = restTemplate;
        this.props = props;
    }

    public BankInstitution institution(String bankCode) {
        return bankRestClient.get()
                .uri("/institutions/{code}", bankCode)
                .retrieve()
                .body(BankInstitution.class);
    }

    public BankStatement fetchStatement(String accountRef) {
        return restTemplate.getForObject(
                props.bankBaseUrl() + "/accounts/" + accountRef + "/statement", BankStatement.class);
    }

    public ProbeResult probe(URI target) {
        return restTemplate.getForObject(target, ProbeResult.class);
    }
}
