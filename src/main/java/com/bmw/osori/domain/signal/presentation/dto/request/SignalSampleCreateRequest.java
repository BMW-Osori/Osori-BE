package com.bmw.osori.domain.signal.presentation.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "RF 신호 샘플 일괄 저장 요청")
public record SignalSampleCreateRequest(

	@Schema(description = "저장할 RF 신호 샘플 목록")
	@NotEmpty(message = "신호 샘플 목록은 비어 있을 수 없습니다.")
	List<@Valid Sample> samples
) {

	@Schema(description = "RF 신호 샘플")
	public record Sample(

		@Schema(description = "수신 신호 세기", example = "-72")
		@NotNull(message = "RSSI는 필수입니다.")
		Integer rssi,

		@Schema(description = "추정 거리(m)", example = "8.1")
		@NotNull(message = "추정 거리는 필수입니다.")
		@PositiveOrZero(message = "추정 거리는 0 이상이어야 합니다.")
		Double estimatedDistance,

		@Schema(description = "측정 시각", example = "2026-09-13T11:30:13.100")
		@NotNull(message = "측정 시각은 필수입니다.")
		LocalDateTime measuredAt
	) {
	}
}
