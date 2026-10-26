package com.bmw.osori.domain.notification.domain;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.global.entity.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "notification_history")
public class NotificationHistory extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proximity_event_id", nullable = false)
	private ProximityEvent proximityEvent;

	@Column(name = "notification_type", nullable = false, length = 50)
	private String notificationType;

	@Column(nullable = false, length = 100)
	private String title;

	@Column(nullable = false, length = 500)
	private String message;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private NotificationStatus status;

	@Column(name = "sent_at")
	private LocalDateTime sentAt;

	protected NotificationHistory() {
	}

	private NotificationHistory(
		Device device,
		ProximityEvent proximityEvent,
		String notificationType,
		String title,
		String message,
		NotificationStatus status,
		LocalDateTime sentAt
	) {
		this.device = device;
		this.proximityEvent = proximityEvent;
		this.notificationType = notificationType;
		this.title = title;
		this.message = message;
		this.status = status;
		this.sentAt = sentAt;
	}

	public static NotificationHistory create(
		Device device,
		ProximityEvent proximityEvent,
		String notificationType,
		String title,
		String message,
		NotificationStatus status,
		LocalDateTime sentAt
	) {
		return new NotificationHistory(device, proximityEvent, notificationType, title, message, status, sentAt);
	}

	public Long getId() {
		return id;
	}

	public Device getDevice() {
		return device;
	}

	public ProximityEvent getProximityEvent() {
		return proximityEvent;
	}

	public String getNotificationType() {
		return notificationType;
	}

	public String getTitle() {
		return title;
	}

	public String getMessage() {
		return message;
	}

	public NotificationStatus getStatus() {
		return status;
	}

	public LocalDateTime getSentAt() {
		return sentAt;
	}
}
