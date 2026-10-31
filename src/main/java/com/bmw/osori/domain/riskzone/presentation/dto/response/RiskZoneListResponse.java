package com.bmw.osori.domain.riskzone.presentation.dto.response;

import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.domain.RiskZone;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(description = "주변 위험구역 목록 조회 응답")
public record RiskZoneListResponse(
	@Schema(description = "위험구역 목록")
	List<RiskZoneResponse> riskZones
) {

	@Schema(description = "위험구역 목록 항목")
	public record RiskZoneResponse(
		@Schema(description = "위험구역 ID", example = "1")
		Long riskZoneId,

		@Schema(description = "위험구역 이름", example = "어린이보호구역 인근 교차로")
		String name,

		@Schema(description = "위험구역 중심 위도", example = "37.5665")
		Double latitude,

		@Schema(description = "위험구역 중심 경도", example = "126.9780")
		Double longitude,

		@Schema(description = "위험 반경(m)", example = "100")
		Integer radius,

		@Schema(description = "현재 위치에서 위험구역 중심까지의 거리(m)", example = "84.3")
		Double distance,

		@Schema(description = "위험도 점수", example = "87.5")
		Double riskScore,

		@Schema(description = "위험 단계", example = "HIGH")
		RiskLevel riskLevel,

		@Schema(description = "관련 사고 건수", example = "12")
		Integer accidentCount
	) {

		public static RiskZoneResponse from(RiskZone riskZone, double distance) {
			return new RiskZoneResponse(
				riskZone.getId(),
				riskZone.getName(),
				riskZone.getLatitude(),
				riskZone.getLongitude(),
				riskZone.getRadius(),
				distance,
				riskZone.getRiskScore(),
				riskZone.getRiskLevel(),
				riskZone.getAccidentCount()
			);
		}
	}
}
