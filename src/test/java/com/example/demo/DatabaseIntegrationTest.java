package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.demo.model.NotificationRecord;
import com.example.demo.repository.NotificationRecordRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class DatabaseIntegrationTest extends AbstractIntegrationTest {

    @Autowired
    private NotificationRecordRepository repository;

    @Test
    void savesAndLoadsNotification() {
        NotificationRecord saved = repository.save(new NotificationRecord("database-message"));

        assertThat(saved.getId()).isNotNull();
        assertThat(repository.findById(saved.getId()))
                .isPresent()
                .get()
                .extracting(NotificationRecord::getBody)
                .isEqualTo("database-message");
    }
}
