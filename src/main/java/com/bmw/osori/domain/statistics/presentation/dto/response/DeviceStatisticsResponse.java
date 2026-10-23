package com.bmw.osori.domain.statistics.presentation.dto.response;

import com.bmw.osori.domain.proximity.domain.AlertLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.Map;

@Schema(description = "디바이스 차량 접근 감지 통계")
public record DeviceStatisticsResponse(
	@Schema(description = "디바이스 ID", example = "1") Long deviceId,
	@Schema(description = "조회 시작 시각(포함)", example = "2026-09-01T00:00:00", nullable = true)
	LocalDateTime from,
	@Schema(description = "조회 종료 시각(포함)", example = "2026-09-30T23:59:59", nullable = true)
	LocalDateTime to,
	@Schema(description = "전체 감지 횟수", example = "5") Long totalCount,
	@Schema(description = "경고 단계별 감지 횟수. 기록이 없는 단계도 0으로 표시")
	Map<AlertLevel, Long> alertLevelCounts,
	@Schema(description = "진동 경고가 발생한 이벤트 횟수", example = "3") Long vibrationTriggeredCount
) {
}
