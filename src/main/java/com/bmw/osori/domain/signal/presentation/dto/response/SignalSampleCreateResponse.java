package com.bmw.osori.domain.signal.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RF 신호 샘플 일괄 저장 응답")
public record SignalSampleCreateResponse(
	@Schema(description = "차량 접근 이벤트 ID", example = "125")
	Long eventId,

	@Schema(description = "저장된 신호 샘플 수", example = "2")
	Integer savedCount
) {
}
