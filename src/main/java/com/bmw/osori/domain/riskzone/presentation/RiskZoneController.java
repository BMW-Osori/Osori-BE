package com.bmw.osori.domain.riskzone.presentation;

import com.bmw.osori.domain.riskzone.application.RiskZoneService;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneDetailResponse;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/risk-zones")
@Tag(name = "Risk Zone", description = "보행 위험구역 API")
public class RiskZoneController {

	private final RiskZoneService riskZoneService;

	public RiskZoneController(RiskZoneService riskZoneService) {
		this.riskZoneService = riskZoneService;
	}

	@GetMapping
	@Operation(
		summary = "주변 위험구역 조회",
		description = "현재 위치와 검색 반경을 기준으로 주변 보행 위험구역을 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "주변 위험구역 조회 성공",
			content = @Content(
				schema = @Schema(implementation = RiskZoneListResponse.class),
				examples = @ExampleObject(
					name = "조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
					    "riskZones": [
						      {
						        "riskZoneId": 1,
						        "name": "어린이보호구역 인근 교차로",
						        "latitude": 37.5665,
						        "longitude": 126.978,
						        "radius": 100,
					        "distance": 0.0,
						        "riskScore": 87.5,
						        "riskLevel": "HIGH",
						        "accidentCount": 12
						      }
						    ]
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 파라미터 검증 실패")
	})
	public ApiResponse<RiskZoneListResponse> getRiskZones(
		@Parameter(description = "현재 위치 위도", example = "37.5665")
		@NotNull(message = "위도는 필수입니다.")
		@Min(value = -90, message = "위도는 -90 이상이어야 합니다.")
		@Max(value = 90, message = "위도는 90 이하여야 합니다.")
		@RequestParam Double latitude,
		@Parameter(description = "현재 위치 경도", example = "126.9780")
		@NotNull(message = "경도는 필수입니다.")
		@Min(value = -180, message = "경도는 -180 이상이어야 합니다.")
		@Max(value = 180, message = "경도는 180 이하여야 합니다.")
		@RequestParam Double longitude,
		@Parameter(description = "검색 반경(m)", example = "500")
		@NotNull(message = "검색 반경은 필수입니다.")
		@Positive(message = "검색 반경은 1 이상이어야 합니다.")
		@RequestParam Integer radius
	) {
		RiskZoneListResponse response = riskZoneService.getRiskZones(latitude, longitude, radius);

		return ApiResponse.ok(response);
	}

	@GetMapping("/{riskZoneId}")
	@Operation(
		summary = "위험구역 상세 조회",
		description = "위험구역 ID로 위험구역 단건 상세 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "위험구역 상세 조회 성공",
			content = @Content(
				schema = @Schema(implementation = RiskZoneDetailResponse.class),
				examples = @ExampleObject(
					name = "상세 조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
						    "riskZoneId": 1,
						    "name": "어린이보호구역 인근 교차로",
						    "latitude": 37.5665,
						    "longitude": 126.978,
						    "radius": 100,
						    "riskScore": 87.5,
						    "riskLevel": "HIGH",
						    "accidentCount": 12,
						    "modelVersion": "risk-zone-v1.0.0",
						    "updatedAt": "2026-09-13T11:30:18"
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "위험구역을 찾을 수 없음")
	})
	public ApiResponse<RiskZoneDetailResponse> getRiskZone(
		@Parameter(description = "조회할 위험구역 ID", example = "1")
		@PathVariable Long riskZoneId
	) {
		RiskZoneDetailResponse response = riskZoneService.getRiskZone(riskZoneId);

		return ApiResponse.ok(response);
	}
}
