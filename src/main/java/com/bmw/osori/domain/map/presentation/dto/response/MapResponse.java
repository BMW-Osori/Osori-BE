package com.bmw.osori.domain.map.presentation.dto.response;

import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse.RiskZoneResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "지도 표시용 통합 데이터")
public record MapResponse(
	@Schema(description = "조회 반경 내 위험구역")
	List<MapRiskZoneResponse> riskZones,
	@Schema(description = "최근 24시간 내 차량 접근 이벤트, 최신순 최대 100건")
	List<MapEventResponse> recentEvents
) {

	public static MapResponse from(List<RiskZoneResponse> riskZones, List<ProximityEvent> events) {
		return new MapResponse(
			riskZones.stream().map(MapRiskZoneResponse::from).toList(),
			events.stream().map(MapEventResponse::from).toList()
		);
	}

	@Schema(description = "지도 위험구역 표시 항목")
	public record MapRiskZoneResponse(
		@Schema(description = "위험구역 ID", example = "32") Long id,
		@Schema(description = "중심 위도", example = "37.5672") Double latitude,
		@Schema(description = "중심 경도", example = "126.9785") Double longitude,
		@Schema(description = "위험 단계", example = "HIGH") RiskLevel riskLevel,
		@Schema(description = "위험도 점수", example = "87.2") Double riskScore
	) {
		private static MapRiskZoneResponse from(RiskZoneResponse riskZone) {
			return new MapRiskZoneResponse(
				riskZone.riskZoneId(),
				riskZone.latitude(),
				riskZone.longitude(),
				riskZone.riskLevel(),
				riskZone.riskScore()
			);
		}
	}

	@Schema(description = "지도 차량 접근 이벤트 표시 항목")
	public record MapEventResponse(
		@Schema(description = "차량 접근 이벤트 ID", example = "125") Long eventId,
		@Schema(description = "감지 위치 위도", example = "37.5665") Double latitude,
		@Schema(description = "감지 위치 경도", example = "126.9780") Double longitude,
		@Schema(description = "경고 단계", example = "DANGER") AlertLevel alertLevel,
		@Schema(description = "감지 시작 시각", example = "2026-09-13T11:30:13") LocalDateTime occurredAt
	) {
		private static MapEventResponse from(ProximityEvent event) {
			return new MapEventResponse(
				event.getId(),
				event.getLatitude(),
				event.getLongitude(),
				event.getAlertLevel(),
				event.getStartedAt()
			);
		}
	}
}
