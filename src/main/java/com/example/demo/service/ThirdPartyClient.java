package com.example.demo.service;

import com.example.demo.model.ThirdPartyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class ThirdPartyClient {

    private final RestClient restClient;

    public ThirdPartyClient(@Value("${thirdparty.base-url}") String baseUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    public ThirdPartyResponse fetchOrderStatus(String orderId) {
        return restClient.get()
                .uri("/orders/{id}/status", orderId)
                .retrieve()
                .body(ThirdPartyResponse.class);
    }
}
