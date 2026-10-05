package com.bmw.osori.domain.proximity.presentation;

import com.bmw.osori.domain.proximity.application.ProximityEventService;
import com.bmw.osori.domain.proximity.presentation.dto.request.ProximityEventCreateRequest;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventCreateResponse;
import com.bmw.osori.global.response.ApiResponse;
import jakarta.validation.Valid;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/proximity-events")
public class ProximityEventController {

	private final ProximityEventService proximityEventService;

	public ProximityEventController(ProximityEventService proximityEventService) {
		this.proximityEventService = proximityEventService;
	}

	@PostMapping
	public ResponseEntity<ApiResponse<ProximityEventCreateResponse>> createProximityEvent(
		@Valid @RequestBody ProximityEventCreateRequest request
	) {
		ProximityEventCreateResponse response = proximityEventService.createProximityEvent(request);

		return ResponseEntity
			.created(URI.create("/api/v1/proximity-events/" + response.eventId()))
			.body(ApiResponse.created(response));
	}
}
