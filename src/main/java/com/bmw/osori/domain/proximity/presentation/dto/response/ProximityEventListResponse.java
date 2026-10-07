package com.bmw.osori.domain.proximity.presentation.dto.response;

import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "차량 접근 이벤트 목록 조회 항목")
public record ProximityEventListResponse(
	@Schema(description = "차량 접근 이벤트 ID", example = "125")
	Long eventId,

	@Schema(description = "감지 위치 위도", example = "37.5665")
	Double latitude,

	@Schema(description = "감지 위치 경도", example = "126.9780")
	Double longitude,

	@Schema(description = "최종 경고 단계", example = "DANGER")
	AlertLevel alertLevel,

	@Schema(description = "이벤트 중 최소 추정 거리(m)", example = "2.8")
	Double minEstimatedDistance,

	@Schema(description = "디바이스 진동 작동 여부", example = "true")
	Boolean vibrationTriggered,

	@Schema(description = "감지 시작 시각", example = "2026-09-13T11:30:13")
	LocalDateTime startedAt,

	@Schema(description = "감지 종료 시각", example = "2026-09-13T11:30:18")
	LocalDateTime endedAt
) {

	public static ProximityEventListResponse from(ProximityEvent proximityEvent) {
		return new ProximityEventListResponse(
			proximityEvent.getId(),
			proximityEvent.getLatitude(),
			proximityEvent.getLongitude(),
			proximityEvent.getAlertLevel(),
			proximityEvent.getMinEstimatedDistance(),
			proximityEvent.getVibrationTriggered(),
			proximityEvent.getStartedAt(),
			proximityEvent.getEndedAt()
		);
	}
}
