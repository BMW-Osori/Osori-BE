package com.bmw.osori.domain.riskzone.presentation.dto.response;

import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.domain.RiskZone;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "위험구역 상세 조회 응답")
public record RiskZoneDetailResponse(
	@Schema(description = "위험구역 ID", example = "1")
	Long riskZoneId,

	@Schema(description = "위험구역 이름", example = "어린이보호구역 인근 교차로")
	String name,

	@Schema(description = "위험구역 중심 위도", example = "37.5665")
	Double latitude,

	@Schema(description = "위험구역 중심 경도", example = "126.9780")
	Double longitude,

	@Schema(description = "위험구역 반경(m)", example = "100")
	Integer radius,

	@Schema(description = "위험 점수", example = "87.5")
	Double riskScore,

	@Schema(description = "위험 단계", example = "HIGH")
	RiskLevel riskLevel,

	@Schema(description = "사고 발생 횟수", example = "12")
	Integer accidentCount,

	@Schema(description = "위험구역 산정 모델 버전", example = "risk-zone-v1.0.0")
	String modelVersion,

	@Schema(description = "위험구역 갱신 시각", example = "2026-09-13T11:30:18")
	LocalDateTime updatedAt
) {

	public static RiskZoneDetailResponse from(RiskZone riskZone) {
		return new RiskZoneDetailResponse(
			riskZone.getId(),
			riskZone.getName(),
			riskZone.getLatitude(),
			riskZone.getLongitude(),
			riskZone.getRadius(),
			riskZone.getRiskScore(),
			riskZone.getRiskLevel(),
			riskZone.getAccidentCount(),
			riskZone.getModelVersion(),
			riskZone.getUpdatedAt()
		);
	}
}
