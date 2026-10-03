package com.bmw.osori.domain.proximity.presentation.dto.request;

import com.bmw.osori.domain.proximity.domain.AlertLevel;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

public record ProximityEventCreateRequest(

	@NotNull(message = "디바이스 ID는 필수입니다.")
	Long deviceId,

	@NotNull(message = "위도는 필수입니다.")
	@Min(value = -90, message = "위도는 -90 이상이어야 합니다.")
	@Max(value = 90, message = "위도는 90 이하여야 합니다.")
	Double latitude,

	@NotNull(message = "경도는 필수입니다.")
	@Min(value = -180, message = "경도는 -180 이상이어야 합니다.")
	@Max(value = 180, message = "경도는 180 이하여야 합니다.")
	Double longitude,

	@NotNull(message = "최대 RSSI는 필수입니다.")
	Integer maxRssi,

	@NotNull(message = "최소 추정 거리는 필수입니다.")
	@PositiveOrZero(message = "최소 추정 거리는 0 이상이어야 합니다.")
	Double minEstimatedDistance,

	@NotNull(message = "경고 단계는 필수입니다.")
	AlertLevel alertLevel,

	@NotNull(message = "연속 감지 횟수는 필수입니다.")
	@PositiveOrZero(message = "연속 감지 횟수는 0 이상이어야 합니다.")
	Integer consecutiveCount,

	@NotNull(message = "진동 작동 여부는 필수입니다.")
	Boolean vibrationTriggered,

	@NotNull(message = "감지 시작 시각은 필수입니다.")
	LocalDateTime startedAt,

	LocalDateTime endedAt
) {
}
