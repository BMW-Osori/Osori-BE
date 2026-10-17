package com.bmw.osori.domain.safety.presentation.dto.response;

import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.domain.RiskZone;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "안전 요약 조회 응답")
public record SafetySummaryResponse(
	@Schema(description = "디바이스 ID", example = "1")
	Long deviceId,

	@Schema(description = "최근 차량 접근 이벤트 경고 단계", example = "DANGER")
	AlertLevel latestAlertLevel,

	@Schema(description = "최근 차량 접근 이벤트 ID", example = "125")
	Long latestEventId,

	@Schema(description = "주변 위험구역 수", example = "2")
	Integer nearbyRiskZoneCount,

	@Schema(description = "주변 위험구역 중 최고 위험 단계", example = "HIGH")
	RiskLevel highestRiskLevel,

	@Schema(description = "주변 위험구역 목록")
	List<NearbyRiskZoneResponse> nearbyRiskZones
) {

	public static SafetySummaryResponse from(
		Long deviceId,
		ProximityEvent latestEvent,
		List<RiskZone> nearbyRiskZones,
		RiskLevel highestRiskLevel
	) {
		List<NearbyRiskZoneResponse> riskZoneResponses = nearbyRiskZones.stream()
			.map(NearbyRiskZoneResponse::from)
			.toList();

		return new SafetySummaryResponse(
			deviceId,
			latestEvent == null ? null : latestEvent.getAlertLevel(),
			latestEvent == null ? null : latestEvent.getId(),
			riskZoneResponses.size(),
			highestRiskLevel,
			riskZoneResponses
		);
	}

	@Schema(description = "주변 위험구역 요약 응답")
	public record NearbyRiskZoneResponse(
		@Schema(description = "위험구역 ID", example = "1")
		Long riskZoneId,

		@Schema(description = "위험구역 이름", example = "어린이보호구역 인근 교차로")
		String name,

		@Schema(description = "위험 단계", example = "HIGH")
		RiskLevel riskLevel,

		@Schema(description = "위험 점수", example = "87.5")
		Double riskScore
	) {

		private static NearbyRiskZoneResponse from(RiskZone riskZone) {
			return new NearbyRiskZoneResponse(
				riskZone.getId(),
				riskZone.getName(),
				riskZone.getRiskLevel(),
				riskZone.getRiskScore()
			);
		}
	}
}
