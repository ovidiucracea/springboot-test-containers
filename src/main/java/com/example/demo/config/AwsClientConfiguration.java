package com.example.demo.config;

import java.net.URI;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class AwsClientConfiguration {

    @Bean
    SnsClient snsClient(@Value("${aws.region}") String region,
                        @Value("${aws.endpoint}") URI endpoint,
                        @Value("${aws.access-key}") String accessKey,
                        @Value("${aws.secret-key}") String secretKey) {
        return SnsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(endpoint)
                .region(Region.of(region))
                .build();
    }

    @Bean
    SqsClient sqsClient(@Value("${aws.region}") String region,
                        @Value("${aws.endpoint}") URI endpoint,
                        @Value("${aws.access-key}") String accessKey,
                        @Value("${aws.secret-key}") String secretKey) {
        return SqsClient.builder()
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
                .endpointOverride(endpoint)
                .region(Region.of(region))
                .build();
    }
}
