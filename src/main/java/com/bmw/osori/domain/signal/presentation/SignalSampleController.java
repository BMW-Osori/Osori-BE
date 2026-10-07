package com.bmw.osori.domain.signal.presentation;

import com.bmw.osori.domain.signal.application.SignalSampleService;
import com.bmw.osori.domain.signal.presentation.dto.request.SignalSampleCreateRequest;
import com.bmw.osori.domain.signal.presentation.dto.response.SignalSampleCreateResponse;
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
}
