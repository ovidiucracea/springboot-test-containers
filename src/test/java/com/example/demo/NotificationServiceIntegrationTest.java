package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.repository.NotificationRecordRepository;
import com.example.demo.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;

class NotificationServiceIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private NotificationRecordRepository repository;

    @Autowired
    private SqsClient sqsClient;

    @Test
    void sendToQueueAndPersistRecord() {
        String queueUrl = sqsClient.createQueue(CreateQueueRequest.builder()
                        .queueName("service-queue")
                        .build())
                .queueUrl();

        notificationService.sendToQueueAndStore(queueUrl, "service-message");

        String receivedBody = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .maxNumberOfMessages(1)
                        .waitTimeSeconds(2)
                        .build())
                .messages()
                .stream()
                .findFirst()
                .map(message -> message.body())
                .orElseThrow();

        assertThat(receivedBody).isEqualTo("service-message");
        assertThat(repository.findAll())
                .extracting(record -> record.getBody())
                .containsExactly("service-message");
    }
}
