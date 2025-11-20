package com.example.demo.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

@Service
public class OrderStatusPublisher {

    private final ThirdPartyClient thirdPartyClient;
    private final SnsClient snsClient;

    public OrderStatusPublisher(ThirdPartyClient thirdPartyClient, SnsClient snsClient) {
        this.thirdPartyClient = thirdPartyClient;
        this.snsClient = snsClient;
    }

    public boolean publishIfReady(String orderId, String topicArn) {
        if ("READY".equalsIgnoreCase(thirdPartyClient.fetchOrderStatus(orderId).status())) {
            snsClient.publish(PublishRequest.builder()
                    .topicArn(topicArn)
                    .message("Order %s is READY".formatted(orderId))
                    .build());
            return true;
        }
        return false;
    }
}
