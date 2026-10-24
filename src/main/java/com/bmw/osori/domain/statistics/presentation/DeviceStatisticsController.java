package com.bmw.osori.domain.statistics.presentation;

import com.bmw.osori.domain.statistics.application.DeviceStatisticsService;
import com.bmw.osori.domain.statistics.presentation.dto.response.DeviceStatisticsResponse;
import com.bmw.osori.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import java.time.LocalDateTime;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/statistics/devices")
@Tag(name = "Device Statistics", description = "디바이스 감지 통계 API")
public class DeviceStatisticsController {

	private final DeviceStatisticsService deviceStatisticsService;

	public DeviceStatisticsController(DeviceStatisticsService deviceStatisticsService) {
		this.deviceStatisticsService = deviceStatisticsService;
	}

	@GetMapping("/{deviceId}")
	@Operation(
		summary = "디바이스 감지 통계 조회",
		description = "이벤트 시작 시각 기준으로 디바이스의 전체 감지 횟수, 경고 단계별 횟수, 진동 경고 횟수를 조회합니다. from과 to는 포함 경계이며 생략 시 해당 방향에 제한이 없습니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "디바이스 감지 통계 조회 성공",
			content = @Content(
				schema = @Schema(implementation = DeviceStatisticsResponse.class),
				examples = @ExampleObject(
					name = "조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
						    "deviceId": 1,
						    "from": "2026-09-01T00:00:00",
						    "to": "2026-09-30T23:59:59",
						    "totalCount": 5,
						    "alertLevelCounts": {"SAFE": 0, "CAUTION": 2, "DANGER": 3, "RELEASED": 0},
						    "vibrationTriggeredCount": 3
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "조회 기간 또는 요청값이 올바르지 않음"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "디바이스를 찾을 수 없음")
	})
	public ApiResponse<DeviceStatisticsResponse> getDeviceStatistics(
		@Parameter(description = "디바이스 ID", example = "1")
		@Positive(message = "디바이스 ID는 1 이상이어야 합니다.")
		@PathVariable Long deviceId,
		@Parameter(description = "조회 시작 시각(포함)", example = "2026-09-01T00:00:00")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		@RequestParam(required = false) LocalDateTime from,
		@Parameter(description = "조회 종료 시각(포함)", example = "2026-09-30T23:59:59")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		@RequestParam(required = false) LocalDateTime to
	) {
		return ApiResponse.ok(deviceStatisticsService.getDeviceStatistics(deviceId, from, to));
	}
}
