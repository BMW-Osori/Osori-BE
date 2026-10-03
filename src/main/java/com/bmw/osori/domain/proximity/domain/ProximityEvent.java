package com.bmw.osori.domain.proximity.domain;

import com.bmw.osori.domain.device.domain.Device;
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
@Table(name = "proximity_event")
public class ProximityEvent extends BaseTimeEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "device_id", nullable = false)
	private Device device;

	@Column(nullable = false)
	private Double latitude;

	@Column(nullable = false)
	private Double longitude;

	@Column(name = "max_rssi", nullable = false)
	private Integer maxRssi;

	@Column(name = "min_estimated_distance", nullable = false)
	private Double minEstimatedDistance;

	@Enumerated(EnumType.STRING)
	@Column(name = "alert_level", nullable = false, length = 20)
	private AlertLevel alertLevel;

	@Column(name = "consecutive_count", nullable = false)
	private Integer consecutiveCount;

	@Column(name = "vibration_triggered", nullable = false)
	private Boolean vibrationTriggered;

	@Column(name = "started_at", nullable = false)
	private LocalDateTime startedAt;

	@Column(name = "ended_at")
	private LocalDateTime endedAt;

	protected ProximityEvent() {
	}

	private ProximityEvent(
		Device device,
		Double latitude,
		Double longitude,
		Integer maxRssi,
		Double minEstimatedDistance,
		AlertLevel alertLevel,
		Integer consecutiveCount,
		Boolean vibrationTriggered,
		LocalDateTime startedAt,
		LocalDateTime endedAt
	) {
		this.device = device;
		this.latitude = latitude;
		this.longitude = longitude;
		this.maxRssi = maxRssi;
		this.minEstimatedDistance = minEstimatedDistance;
		this.alertLevel = alertLevel;
		this.consecutiveCount = consecutiveCount;
		this.vibrationTriggered = vibrationTriggered;
		this.startedAt = startedAt;
		this.endedAt = endedAt;
	}

	public static ProximityEvent create(
		Device device,
		Double latitude,
		Double longitude,
		Integer maxRssi,
		Double minEstimatedDistance,
		AlertLevel alertLevel,
		Integer consecutiveCount,
		Boolean vibrationTriggered,
		LocalDateTime startedAt,
		LocalDateTime endedAt
	) {
		return new ProximityEvent(
			device,
			latitude,
			longitude,
			maxRssi,
			minEstimatedDistance,
			alertLevel,
			consecutiveCount,
			vibrationTriggered,
			startedAt,
			endedAt
		);
	}

	public Long getId() {
		return id;
	}

	public Device getDevice() {
		return device;
	}

	public Double getLatitude() {
		return latitude;
	}

	public Double getLongitude() {
		return longitude;
	}

	public Integer getMaxRssi() {
		return maxRssi;
	}

	public Double getMinEstimatedDistance() {
		return minEstimatedDistance;
	}

	public AlertLevel getAlertLevel() {
		return alertLevel;
	}

	public Integer getConsecutiveCount() {
		return consecutiveCount;
	}

	public Boolean getVibrationTriggered() {
		return vibrationTriggered;
	}

	public LocalDateTime getStartedAt() {
		return startedAt;
	}

	public LocalDateTime getEndedAt() {
		return endedAt;
	}
}
