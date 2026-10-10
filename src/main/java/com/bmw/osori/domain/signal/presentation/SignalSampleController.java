package com.bmw.osori.domain.signal.presentation;

import com.bmw.osori.domain.signal.application.SignalSampleService;
import com.bmw.osori.domain.signal.presentation.dto.request.SignalSampleCreateRequest;
import com.bmw.osori.domain.signal.presentation.dto.response.SignalSampleCreateResponse;
import com.bmw.osori.domain.signal.presentation.dto.response.SignalSampleListResponse;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/proximity-events/{eventId}/samples")
@Tag(name = "Signal Sample", description = "RF 신호 샘플 API")
public class SignalSampleController {

	private final SignalSampleService signalSampleService;

	public SignalSampleController(SignalSampleService signalSampleService) {
		this.signalSampleService = signalSampleService;
	}

	@PostMapping
	@Operation(
		summary = "RF 신호 샘플 일괄 저장",
		description = "차량 접근 이벤트 발생 과정에서 측정된 RSSI와 추정 거리 샘플을 일괄 저장합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "신호 샘플 저장 성공",
			content = @Content(
				schema = @Schema(implementation = SignalSampleCreateResponse.class),
				examples = @ExampleObject(
					name = "저장 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_201",
						  "message": "요청이 성공적으로 생성되었습니다.",
						  "data": {
						    "eventId": 125,
						    "savedCount": 2
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "차량 접근 이벤트를 찾을 수 없음")
	})
	public ResponseEntity<ApiResponse<SignalSampleCreateResponse>> createSignalSamples(
		@Parameter(description = "신호 샘플을 저장할 차량 접근 이벤트 ID", example = "125")
		@PathVariable Long eventId,
		@Valid @RequestBody SignalSampleCreateRequest request
	) {
		SignalSampleCreateResponse response = signalSampleService.createSignalSamples(eventId, request);

		return ResponseEntity
			.created(URI.create("/api/v1/proximity-events/" + response.eventId() + "/samples"))
			.body(ApiResponse.created(response));
	}

	@GetMapping
	@Operation(
		summary = "RF 신호 샘플 목록 조회",
		description = "특정 차량 접근 이벤트에 저장된 RSSI와 추정 거리 샘플을 측정 시각 오름차순으로 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "신호 샘플 목록 조회 성공",
			content = @Content(
				schema = @Schema(implementation = SignalSampleListResponse.class),
				examples = @ExampleObject(
					name = "조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
						    "eventId": 125,
						    "samples": [
						      {
						        "sampleId": 1,
						        "rssi": -72,
						        "estimatedDistance": 8.1,
						        "measuredAt": "2026-09-13T11:30:13.100"
						      },
						      {
						        "sampleId": 2,
						        "rssi": -66,
						        "estimatedDistance": 5.4,
						        "measuredAt": "2026-09-13T11:30:13.300"
						      }
						    ]
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "차량 접근 이벤트를 찾을 수 없음")
	})
	public ApiResponse<SignalSampleListResponse> getSignalSamples(
		@Parameter(description = "신호 샘플을 조회할 차량 접근 이벤트 ID", example = "125")
		@PathVariable Long eventId
	) {
		SignalSampleListResponse response = signalSampleService.getSignalSamples(eventId);

		return ApiResponse.ok(response);
	}
}
