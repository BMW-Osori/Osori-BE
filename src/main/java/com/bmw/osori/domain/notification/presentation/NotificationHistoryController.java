package com.bmw.osori.domain.notification.presentation;

import com.bmw.osori.domain.notification.application.NotificationHistoryService;
import com.bmw.osori.domain.notification.presentation.dto.request.NotificationHistoryCreateRequest;
import com.bmw.osori.domain.notification.presentation.dto.response.NotificationHistoryPageResponse;
import com.bmw.osori.domain.notification.presentation.dto.response.NotificationHistoryResponse;
import com.bmw.osori.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/v1/notification-histories")
@Tag(name = "Notification History", description = "보조 알림 이력 API")
public class NotificationHistoryController {

	private final NotificationHistoryService notificationHistoryService;

	public NotificationHistoryController(NotificationHistoryService notificationHistoryService) {
		this.notificationHistoryService = notificationHistoryService;
	}

	@PostMapping
	@Operation(
		summary = "알림 이력 저장",
		description = "스마트폰 앱에서 보조 알림의 발송 결과를 보고받아 저장합니다. SENT는 sentAt이 필수이고 FAILED는 sentAt을 생략합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "201",
			description = "알림 이력 저장 성공",
			content = @Content(
				schema = @Schema(implementation = NotificationHistoryResponse.class),
				examples = @ExampleObject(
					name = "저장 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_201",
						  "message": "요청이 성공적으로 생성되었습니다.",
						  "data": {
						    "notificationHistoryId": 1,
						    "deviceId": 1,
						    "proximityEventId": 125,
						    "notificationType": "PROXIMITY_ALERT",
						    "title": "차량 접근 경고",
						    "message": "근처에 차량이 접근하고 있습니다.",
						    "status": "SENT",
						    "sentAt": "2026-09-13T11:30:18",
						    "createdAt": "2026-09-13T11:30:19"
						  }
						}
						"""
				)
			)
		),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "요청값 검증 실패 또는 디바이스와 이벤트 불일치"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "디바이스 또는 차량 접근 이벤트를 찾을 수 없음")
	})
	public ResponseEntity<ApiResponse<NotificationHistoryResponse>> createNotificationHistory(
		@Valid @RequestBody NotificationHistoryCreateRequest request
	) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.created(notificationHistoryService.createNotificationHistory(request)));
	}

	@GetMapping
	@Operation(
		summary = "디바이스 알림 이력 조회",
		description = "지정한 디바이스의 알림 이력을 저장 시각 내림차순으로 페이지 조회합니다."
	)
	@ApiResponses(value = {
		@io.swagger.v3.oas.annotations.responses.ApiResponse(
			responseCode = "200",
			description = "알림 이력 조회 성공",
			content = @Content(
				schema = @Schema(implementation = NotificationHistoryPageResponse.class),
				examples = @ExampleObject(
					name = "조회 성공",
					value = """
						{
						  "success": true,
						  "code": "COMMON_200",
						  "message": "요청에 성공했습니다.",
						  "data": {
						    "content": [
						      {"notificationHistoryId": 1, "deviceId": 1, "proximityEventId": 125,
						       "notificationType": "PROXIMITY_ALERT", "title": "차량 접근 경고",
						       "message": "근처에 차량이 접근하고 있습니다.", "status": "SENT",
						       "sentAt": "2026-09-13T11:30:18", "createdAt": "2026-09-13T11:30:19"}
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
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "조회 파라미터 검증 실패"),
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "디바이스를 찾을 수 없음")
	})
	public ApiResponse<NotificationHistoryPageResponse> getNotificationHistories(
		@Parameter(description = "조회할 디바이스 ID", example = "1")
		@Positive(message = "디바이스 ID는 1 이상이어야 합니다.")
		@RequestParam Long deviceId,
		@Parameter(description = "페이지 번호(0부터 시작)", example = "0")
		@Min(value = 0, message = "페이지 번호는 0 이상이어야 합니다.")
		@RequestParam(defaultValue = "0") Integer page,
		@Parameter(description = "페이지 크기(1~100)", example = "20")
		@Min(value = 1, message = "페이지 크기는 1 이상이어야 합니다.")
		@Max(value = 100, message = "페이지 크기는 100 이하여야 합니다.")
		@RequestParam(defaultValue = "20") Integer size
	) {
		return ApiResponse.ok(notificationHistoryService.getNotificationHistories(
			deviceId, PageRequest.of(page, size)
		));
	}
}
