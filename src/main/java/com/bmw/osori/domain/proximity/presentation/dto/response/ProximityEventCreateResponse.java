package com.bmw.osori.domain.proximity.presentation.dto.response;

import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import java.time.LocalDateTime;

public record ProximityEventCreateResponse(
	Long eventId,
	AlertLevel alertLevel,
	Boolean vibrationTriggered,
	LocalDateTime createdAt
) {

	public static ProximityEventCreateResponse from(ProximityEvent proximityEvent) {
		return new ProximityEventCreateResponse(
			proximityEvent.getId(),
			proximityEvent.getAlertLevel(),
			proximityEvent.getVibrationTriggered(),
			proximityEvent.getCreatedAt()
		);
	}
}
