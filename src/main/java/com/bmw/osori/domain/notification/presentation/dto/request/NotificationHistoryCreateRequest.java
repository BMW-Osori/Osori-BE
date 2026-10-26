package com.bmw.osori.domain.notification.presentation.dto.request;

import com.bmw.osori.domain.notification.domain.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Schema(description = "앱에서 전달한 알림 발송 결과 저장 요청")
public record NotificationHistoryCreateRequest(
	@Schema(description = "대상 디바이스 ID", example = "1")
	@NotNull(message = "디바이스 ID는 필수입니다.")
	@Positive(message = "디바이스 ID는 1 이상이어야 합니다.")
	Long deviceId,
	@Schema(description = "관련 차량 접근 이벤트 ID", example = "125")
	@NotNull(message = "차량 접근 이벤트 ID는 필수입니다.")
	@Positive(message = "차량 접근 이벤트 ID는 1 이상이어야 합니다.")
	Long proximityEventId,
	@Schema(description = "알림 유형", example = "PROXIMITY_ALERT")
	@NotBlank(message = "알림 유형은 필수입니다.")
	@Size(max = 50, message = "알림 유형은 50자 이하여야 합니다.")
	String notificationType,
	@Schema(description = "알림 제목", example = "차량 접근 경고")
	@NotBlank(message = "알림 제목은 필수입니다.")
	@Size(max = 100, message = "알림 제목은 100자 이하여야 합니다.")
	String title,
	@Schema(description = "알림 내용", example = "근처에 차량이 접근하고 있습니다.")
	@NotBlank(message = "알림 내용은 필수입니다.")
	@Size(max = 500, message = "알림 내용은 500자 이하여야 합니다.")
	String message,
	@Schema(description = "발송 결과", example = "SENT")
	@NotNull(message = "발송 상태는 필수입니다.")
	NotificationStatus status,
	@Schema(description = "실제 발송 시각. SENT일 때 필수, FAILED일 때 생략", example = "2026-09-13T11:30:18", nullable = true)
	LocalDateTime sentAt
) {
}
