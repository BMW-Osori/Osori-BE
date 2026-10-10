package com.bmw.osori.domain.signal.presentation.dto.response;

import com.bmw.osori.domain.signal.domain.SignalSample;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "RF 신호 샘플 목록 조회 응답")
public record SignalSampleListResponse(
	@Schema(description = "차량 접근 이벤트 ID", example = "125")
	Long eventId,

	@Schema(description = "RF 신호 샘플 목록")
	List<SignalSampleResponse> samples
) {

	public static SignalSampleListResponse of(Long eventId, List<SignalSample> signalSamples) {
		List<SignalSampleResponse> responses = signalSamples.stream()
			.map(SignalSampleResponse::from)
			.toList();

		return new SignalSampleListResponse(eventId, responses);
	}

	@Schema(description = "RF 신호 샘플 응답")
	public record SignalSampleResponse(
		@Schema(description = "신호 샘플 ID", example = "1")
		Long sampleId,

		@Schema(description = "수신 신호 세기", example = "-72")
		Integer rssi,

		@Schema(description = "추정 거리(m)", example = "8.1")
		Double estimatedDistance,

		@Schema(description = "측정 시각", example = "2026-09-13T11:30:13.100")
		LocalDateTime measuredAt
	) {

		private static SignalSampleResponse from(SignalSample signalSample) {
			return new SignalSampleResponse(
				signalSample.getId(),
				signalSample.getRssi(),
				signalSample.getEstimatedDistance(),
				signalSample.getMeasuredAt()
			);
		}
	}
}
