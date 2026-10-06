package com.bmw.osori.domain.signal.domain;

import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "signal_sample")
public class SignalSample {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proximity_event_id", nullable = false)
	private ProximityEvent proximityEvent;

	@Column(nullable = false)
	private Integer rssi;

	@Column(name = "estimated_distance", nullable = false)
	private Double estimatedDistance;

	@Column(name = "measured_at", nullable = false)
	private LocalDateTime measuredAt;

	protected SignalSample() {
	}

	private SignalSample(
		ProximityEvent proximityEvent,
		Integer rssi,
		Double estimatedDistance,
		LocalDateTime measuredAt
	) {
		this.proximityEvent = proximityEvent;
		this.rssi = rssi;
		this.estimatedDistance = estimatedDistance;
		this.measuredAt = measuredAt;
	}

	public static SignalSample create(
		ProximityEvent proximityEvent,
		Integer rssi,
		Double estimatedDistance,
		LocalDateTime measuredAt
	) {
		return new SignalSample(
			proximityEvent,
			rssi,
			estimatedDistance,
			measuredAt
		);
	}

	public Long getId() {
		return id;
	}

	public ProximityEvent getProximityEvent() {
		return proximityEvent;
	}

	public Integer getRssi() {
		return rssi;
	}

	public Double getEstimatedDistance() {
		return estimatedDistance;
	}

	public LocalDateTime getMeasuredAt() {
		return measuredAt;
	}
}
