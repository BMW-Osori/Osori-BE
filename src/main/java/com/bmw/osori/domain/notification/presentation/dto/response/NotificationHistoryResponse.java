package com.bmw.osori.domain.notification.presentation.dto.response;

import com.bmw.osori.domain.notification.domain.NotificationHistory;
import com.bmw.osori.domain.notification.domain.NotificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

@Schema(description = "알림 이력")
public record NotificationHistoryResponse(
	@Schema(description = "알림 이력 ID", example = "1") Long notificationHistoryId,
	@Schema(description = "디바이스 ID", example = "1") Long deviceId,
	@Schema(description = "차량 접근 이벤트 ID", example = "125") Long proximityEventId,
	@Schema(description = "알림 유형", example = "PROXIMITY_ALERT") String notificationType,
	@Schema(description = "알림 제목", example = "차량 접근 경고") String title,
	@Schema(description = "알림 내용", example = "근처에 차량이 접근하고 있습니다.") String message,
	@Schema(description = "발송 결과", example = "SENT") NotificationStatus status,
	@Schema(description = "실제 발송 시각", example = "2026-09-13T11:30:18", nullable = true) LocalDateTime sentAt,
	@Schema(description = "이력 저장 시각", example = "2026-09-13T11:30:19") LocalDateTime createdAt
) {

	public static NotificationHistoryResponse from(NotificationHistory history) {
		return new NotificationHistoryResponse(
			history.getId(),
			history.getDevice().getId(),
			history.getProximityEvent().getId(),
			history.getNotificationType(),
			history.getTitle(),
			history.getMessage(),
			history.getStatus(),
			history.getSentAt(),
			history.getCreatedAt()
		);
	}
}
