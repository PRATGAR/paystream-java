package com.ledgerline.payments.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class WebhookDispatcher {

    private static final Logger log = LoggerFactory.getLogger(WebhookDispatcher.class);

    private final RestTemplate restTemplate;

    public WebhookDispatcher(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void send(String url, Object payload) {
        try {
            restTemplate.postForObject(url, payload, String.class);
        } catch (RestClientException e) {
            log.warn("webhook delivery failed: {}", e.getMessage());
        }
    }
}
