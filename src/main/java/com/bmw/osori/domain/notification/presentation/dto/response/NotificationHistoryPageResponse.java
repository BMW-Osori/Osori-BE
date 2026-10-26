package com.bmw.osori.domain.notification.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(description = "알림 이력 페이지")
public record NotificationHistoryPageResponse(
	@Schema(description = "현재 페이지의 알림 이력") List<NotificationHistoryResponse> content,
	@Schema(description = "현재 페이지 번호", example = "0") Integer page,
	@Schema(description = "페이지 크기", example = "20") Integer size,
	@Schema(description = "전체 알림 이력 수", example = "1") Long totalElements
) {

	public static NotificationHistoryPageResponse from(Page<NotificationHistoryResponse> page) {
		return new NotificationHistoryPageResponse(
			page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements()
		);
	}
}
