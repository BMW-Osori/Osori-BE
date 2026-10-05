package com.bmw.osori.domain.proximity.presentation;

import com.bmw.osori.domain.proximity.application.ProximityEventService;
import com.bmw.osori.domain.proximity.presentation.dto.request.ProximityEventCreateRequest;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventCreateResponse;
import com.bmw.osori.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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
}
