package com.bmw.osori.domain.device.presentation;

import com.bmw.osori.domain.device.application.DeviceService;
import com.bmw.osori.domain.device.presentation.dto.request.DeviceCreateRequest;
import com.bmw.osori.domain.device.presentation.dto.response.DeviceCreateResponse;
import com.bmw.osori.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

	private final DeviceService deviceService;

	public DeviceController(DeviceService deviceService) {
		this.deviceService = deviceService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<DeviceCreateResponse>> createDevice(
		@Valid @RequestBody DeviceCreateRequest request
	) {
		DeviceCreateResponse response = deviceService.createDevice(request);

		return ResponseEntity
			.created(URI.create("/api/v1/devices/" + response.deviceId()))
			.body(ApiResponse.created(response));
	}
}
