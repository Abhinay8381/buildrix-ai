package com.abhinay.buildrix_ai.webhook;

public interface WebhookService {

    void processWebhook(String payload, String sigHeader);
}
