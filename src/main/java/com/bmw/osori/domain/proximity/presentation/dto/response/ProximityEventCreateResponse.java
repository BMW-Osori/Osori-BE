package com.bmw.osori.domain.proximity.presentation.dto.response;

import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "차량 접근 이벤트 저장 응답")
public record ProximityEventCreateResponse(
	@Schema(description = "차량 접근 이벤트 ID", example = "125")
	Long eventId,

	@Schema(description = "최종 경고 단계", example = "DANGER")
	AlertLevel alertLevel,

	@Schema(description = "디바이스 진동 작동 여부", example = "true")
	Boolean vibrationTriggered,

	@Schema(description = "이벤트 저장 시각", example = "2026-09-13T11:30:18")
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
