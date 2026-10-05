package com.bmw.osori.domain.proximity.presentation.dto.request;

import com.bmw.osori.domain.proximity.domain.AlertLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Schema(description = "차량 접근 이벤트 저장 요청")
public record ProximityEventCreateRequest(

	@Schema(description = "차량 접근을 감지한 디바이스 ID", example = "1")
	@NotNull(message = "디바이스 ID는 필수입니다.")
	Long deviceId,

	@Schema(description = "감지 위치 위도", example = "37.5665")
	@NotNull(message = "위도는 필수입니다.")
	@Min(value = -90, message = "위도는 -90 이상이어야 합니다.")
	@Max(value = 90, message = "위도는 90 이하여야 합니다.")
	Double latitude,

	@Schema(description = "감지 위치 경도", example = "126.9780")
	@NotNull(message = "경도는 필수입니다.")
	@Min(value = -180, message = "경도는 -180 이상이어야 합니다.")
	@Max(value = 180, message = "경도는 180 이하여야 합니다.")
	Double longitude,

	@Schema(description = "이벤트 중 가장 강한 RSSI", example = "-53")
	@NotNull(message = "최대 RSSI는 필수입니다.")
	Integer maxRssi,

	@Schema(description = "이벤트 중 최소 추정 거리(m)", example = "2.8")
	@NotNull(message = "최소 추정 거리는 필수입니다.")
	@PositiveOrZero(message = "최소 추정 거리는 0 이상이어야 합니다.")
	Double minEstimatedDistance,

	@Schema(description = "최종 경고 단계", example = "DANGER")
	@NotNull(message = "경고 단계는 필수입니다.")
	AlertLevel alertLevel,

	@Schema(description = "연속 감지 횟수", example = "5")
	@NotNull(message = "연속 감지 횟수는 필수입니다.")
	@PositiveOrZero(message = "연속 감지 횟수는 0 이상이어야 합니다.")
	Integer consecutiveCount,

	@Schema(description = "디바이스 진동 작동 여부", example = "true")
	@NotNull(message = "진동 작동 여부는 필수입니다.")
	Boolean vibrationTriggered,

	@Schema(description = "감지 시작 시각", example = "2026-09-13T11:30:13")
	@NotNull(message = "감지 시작 시각은 필수입니다.")
	LocalDateTime startedAt,

	@Schema(description = "감지 종료 시각", example = "2026-09-13T11:30:18", nullable = true)
	LocalDateTime endedAt
) {
}
