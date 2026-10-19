package com.bmw.osori.domain.map.presentation;

import com.bmw.osori.domain.map.application.MapService;
import com.bmw.osori.domain.map.presentation.dto.response.MapResponse;
import com.bmw.osori.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/map")
@Tag(name = "Map", description = "지도 표시용 데이터 API")
public class MapController {

	private final MapService mapService;

	public MapController(MapService mapService) {
		this.mapService = mapService;
	}

	@GetMapping
	@Operation(
		summary = "지도 통합 데이터 조회",
		description = "중심 위치의 반경 내 위험구역과 최근 24시간 차량 접근 이벤트를 조회합니다. 이벤트는 최신순 최대 100건입니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "지도 통합 데이터 조회 성공",
			content = @Content(
				schema = @Schema(implementation = MapResponse.class),
				examples = @ExampleObject(
					name = "조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
						    "riskZones": [
						      {"id": 32, "latitude": 37.5672, "longitude": 126.9785, "riskLevel": "HIGH", "riskScore": 87.2}
						    ],
						    "recentEvents": [
						      {"eventId": 125, "latitude": 37.5665, "longitude": 126.9780, "alertLevel": "DANGER", "occurredAt": "2026-09-13T11:30:13"}
						    ]
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 파라미터 검증 실패")
	})
	public ApiResponse<MapResponse> getMap(
		@Parameter(description = "지도 중심 위도", example = "37.5665")
		@NotNull(message = "위도는 필수입니다.")
		@Min(value = -90, message = "위도는 -90 이상이어야 합니다.")
		@Max(value = 90, message = "위도는 90 이하여야 합니다.")
		@RequestParam Double latitude,
		@Parameter(description = "지도 중심 경도", example = "126.9780")
		@NotNull(message = "경도는 필수입니다.")
		@Min(value = -180, message = "경도는 -180 이상이어야 합니다.")
		@Max(value = 180, message = "경도는 180 이하여야 합니다.")
		@RequestParam Double longitude,
		@Parameter(description = "조회 반경(m)", example = "1000")
		@NotNull(message = "조회 반경은 필수입니다.")
		@Positive(message = "조회 반경은 1 이상이어야 합니다.")
		@RequestParam Integer radius
	) {
		return ApiResponse.ok(mapService.getMap(latitude, longitude, radius));
	}
}
