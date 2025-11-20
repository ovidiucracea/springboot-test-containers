package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.example.demo.service.OrderStatusPublisher;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.CreateTopicRequest;
import software.amazon.awssdk.services.sns.model.SubscribeRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;

class AwsMessagingIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private SnsClient snsClient;

    @Autowired
    private SqsClient sqsClient;

    @Autowired
    private OrderStatusPublisher orderStatusPublisher;

    @Test
    void publishesToSnsAndFanOutToSqs() {
        String queueUrl = sqsClient.createQueue(CreateQueueRequest.builder()
                        .queueName("demo-queue")
                        .build())
                .queueUrl();
        String topicArn = snsClient.createTopic(CreateTopicRequest.builder()
                        .name("demo-topic")
                        .build())
                .topicArn();

        String queueArn = sqsClient.getQueueAttributes(builder -> builder.queueUrl(queueUrl).attributeNames("QueueArn")).attributes().get("QueueArn");
        sqsClient.setQueueAttributes(SetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributesEntry("Policy", "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":\"*\",\"Action\":\"sqs:SendMessage\",\"Resource\":\"" + queueArn + "\",\"Condition\":{\"ArnEquals\":{\"aws:SourceArn\":\"" + topicArn + "\"}}}]}")
                .build());

        snsClient.subscribe(SubscribeRequest.builder()
                .topicArn(topicArn)
                .protocol("sqs")
                .endpoint(queueArn)
                .build());

        orderStatusPublisher.publishIfReady("42", topicArn);

        List<Message> messages = sqsClient.receiveMessage(ReceiveMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .waitTimeSeconds((int) Duration.ofSeconds(5).toSeconds())
                        .maxNumberOfMessages(1)
                        .build())
                .messages();

        assertThat(messages)
                .singleElement()
                .extracting(Message::body)
                .isEqualTo("Order 42 is READY");
    }
}
