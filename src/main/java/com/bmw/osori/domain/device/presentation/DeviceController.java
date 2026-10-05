package com.bmw.osori.domain.device.presentation;

import com.bmw.osori.domain.device.application.DeviceService;
import com.bmw.osori.domain.device.presentation.dto.request.DeviceCreateRequest;
import com.bmw.osori.domain.device.presentation.dto.response.DeviceCreateResponse;
import com.bmw.osori.domain.device.presentation.dto.response.DeviceResponse;
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
@RequestMapping("/api/v1/devices")
@Tag(name = "Device", description = "보행자 수신 디바이스 API")
public class DeviceController {

	private final DeviceService deviceService;

	public DeviceController(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@PostMapping
	@Operation(
		summary = "디바이스 등록",
		description = "보행자가 사용하는 수신 디바이스를 등록합니다. 등록된 디바이스는 차량 접근 이벤트 저장 시 참조됩니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "디바이스 등록 성공",
			content = @Content(
				schema = @Schema(implementation = DeviceCreateResponse.class),
				examples = @ExampleObject(
					name = "등록 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_201",
						  "message": "요청이 성공적으로 생성되었습니다.",
						  "data": {
						    "deviceId": 1,
						    "deviceUuid": "BADGER-DEVICE-001",
						    "name": "전시용 수신기",
						    "deviceType": "WEARABLE",
						    "status": "ACTIVE",
						    "createdAt": "2026-09-13T11:30:00"
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 검증 실패"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 디바이스")
	})
	public ResponseEntity<ApiResponse<DeviceCreateResponse>> createDevice(
		@Valid @RequestBody DeviceCreateRequest request
	) {
		DeviceCreateResponse response = deviceService.createDevice(request);

		return ResponseEntity
			.created(URI.create("/api/v1/devices/" + response.deviceId()))
			.body(ApiResponse.created(response));
	}

	@GetMapping("/{deviceId}")
	@Operation(
		summary = "디바이스 단건 조회",
		description = "디바이스 ID로 등록된 수신 디바이스 정보를 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "디바이스 조회 성공",
			content = @Content(
				schema = @Schema(implementation = DeviceResponse.class),
				examples = @ExampleObject(
					name = "조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
						    "deviceId": 1,
						    "deviceUuid": "BADGER-DEVICE-001",
						    "name": "전시용 수신기",
						    "deviceType": "WEARABLE",
						    "status": "ACTIVE"
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "디바이스를 찾을 수 없음")
	})
	public ApiResponse<DeviceResponse> getDevice(
		@Parameter(description = "조회할 디바이스 ID", example = "1")
		@PathVariable Long deviceId
	) {
		DeviceResponse response = deviceService.getDevice(deviceId);

		return ApiResponse.ok(response);
	}
}
