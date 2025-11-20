package com.example.demo.repository;

import com.example.demo.model.NotificationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRecordRepository extends JpaRepository<NotificationRecord, Long> {
}
