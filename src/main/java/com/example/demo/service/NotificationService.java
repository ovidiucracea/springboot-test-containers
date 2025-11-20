package com.example.demo.service;

import com.example.demo.model.NotificationRecord;
import com.example.demo.repository.NotificationRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class NotificationService {

    private final SqsClient sqsClient;
    private final NotificationRecordRepository notificationRecordRepository;

    public NotificationService(SqsClient sqsClient, NotificationRecordRepository notificationRecordRepository) {
        this.sqsClient = sqsClient;
        this.notificationRecordRepository = notificationRecordRepository;
    }

    @Transactional
    public void sendToQueueAndStore(String queueUrl, String body) {
        sqsClient.sendMessage(SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(body)
                .build());
        notificationRecordRepository.save(new NotificationRecord(body));
    }
}
