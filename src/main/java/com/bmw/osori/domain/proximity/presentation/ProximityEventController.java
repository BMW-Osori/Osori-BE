package com.bmw.osori.domain.proximity.presentation;

import com.bmw.osori.domain.proximity.application.ProximityEventService;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.presentation.dto.request.ProximityEventCreateRequest;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventCreateResponse;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventDetailResponse;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventListResponse;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventPageResponse;
import com.bmw.osori.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.time.LocalDateTime;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/proximity-events")
@Tag(name = "Proximity Event", description = "차량 접근 이벤트 API")
public class ProximityEventController {

	private final ProximityEventService proximityEventService;

	public ProximityEventController(ProximityEventService proximityEventService) {
		this.proximityEventService = proximityEventService;
	}

	@PostMapping
	@Operation(
		summary = "차량 접근 이벤트 저장",
		description = "보행자 디바이스에서 감지한 차량 접근 이벤트를 저장합니다. 핵심 진동 경고는 디바이스에서 처리하고, 서버는 감지 결과와 위치 정보를 저장합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "차량 접근 이벤트 저장 성공",
			content = @Content(
				schema = @Schema(implementation = ProximityEventCreateResponse.class),
				examples = @ExampleObject(
					name = "저장 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_201",
						  "message": "요청이 성공적으로 생성되었습니다.",
						  "data": {
						    "eventId": 125,
						    "alertLevel": "DANGER",
						    "vibrationTriggered": true,
						    "createdAt": "2026-09-13T11:30:18"
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "디바이스를 찾을 수 없음")
	})
	public ResponseEntity<ApiResponse<ProximityEventCreateResponse>> createProximityEvent(
		@Valid @RequestBody ProximityEventCreateRequest request
	) {
		ProximityEventCreateResponse response = proximityEventService.createProximityEvent(request);

		return ResponseEntity
			.created(URI.create("/api/v1/proximity-events/" + response.eventId()))
			.body(ApiResponse.created(response));
	}

	@GetMapping
	@Operation(
		summary = "차량 접근 이벤트 목록 조회",
		description = "저장된 차량 접근 이벤트 이력을 디바이스, 기간, 경고 단계 조건으로 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "차량 접근 이벤트 목록 조회 성공",
			content = @Content(
				schema = @Schema(implementation = ProximityEventPageResponse.class),
				examples = @ExampleObject(
					name = "목록 조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
						    "content": [
						      {
						        "eventId": 125,
						        "latitude": 37.5665,
						        "longitude": 126.978,
						        "alertLevel": "DANGER",
						        "minEstimatedDistance": 2.8,
						        "vibrationTriggered": true,
						        "startedAt": "2026-09-13T11:30:13",
						        "endedAt": "2026-09-13T11:30:18"
						      }
						    ],
						    "page": 0,
						    "size": 20,
						    "totalElements": 1
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청 파라미터 타입 오류")
	})
	public ApiResponse<ProximityEventPageResponse<ProximityEventListResponse>> getProximityEvents(
		@Parameter(description = "특정 디바이스 ID", example = "1")
		@RequestParam(required = false) Long deviceId,
		@Parameter(description = "조회 시작 시각", example = "2026-09-01T00:00:00")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		@RequestParam(required = false) LocalDateTime from,
		@Parameter(description = "조회 종료 시각", example = "2026-09-30T23:59:59")
		@DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
		@RequestParam(required = false) LocalDateTime to,
		@Parameter(description = "경고 단계", example = "DANGER")
		@RequestParam(required = false) AlertLevel alertLevel,
		@ParameterObject Pageable pageable
	) {
		ProximityEventPageResponse<ProximityEventListResponse> response = proximityEventService.getProximityEvents(
			deviceId,
			from,
			to,
			alertLevel,
			pageable
		);

		return ApiResponse.ok(response);
	}

	@GetMapping("/{eventId}")
	@Operation(
		summary = "차량 접근 이벤트 상세 조회",
		description = "차량 접근 이벤트 ID로 단건 상세 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "차량 접근 이벤트 상세 조회 성공",
			content = @Content(
				schema = @Schema(implementation = ProximityEventDetailResponse.class),
				examples = @ExampleObject(
					name = "상세 조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
						    "eventId": 125,
						    "deviceId": 1,
						    "latitude": 37.5665,
						    "longitude": 126.978,
						    "maxRssi": -53,
						    "minEstimatedDistance": 2.8,
						    "alertLevel": "DANGER",
						    "consecutiveCount": 5,
						    "vibrationTriggered": true,
						    "startedAt": "2026-09-13T11:30:13",
						    "endedAt": "2026-09-13T11:30:18"
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "차량 접근 이벤트를 찾을 수 없음")
	})
	public ApiResponse<ProximityEventDetailResponse> getProximityEvent(
		@Parameter(description = "조회할 차량 접근 이벤트 ID", example = "125")
		@PathVariable Long eventId
	) {
		ProximityEventDetailResponse response = proximityEventService.getProximityEvent(eventId);

		return ApiResponse.ok(response);
	}
}
