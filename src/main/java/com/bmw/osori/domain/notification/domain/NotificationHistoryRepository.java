package com.bmw.osori.domain.notification.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationHistoryRepository extends JpaRepository<NotificationHistory, Long> {

	Page<NotificationHistory> findAllByDevice_IdOrderByCreatedAtDescIdDesc(Long deviceId, Pageable pageable);
}
