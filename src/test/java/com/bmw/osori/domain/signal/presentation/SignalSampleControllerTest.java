package com.bmw.osori.domain.signal.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.proximity.exception.ProximityEventErrorCode;
import com.bmw.osori.domain.signal.application.SignalSampleService;
import com.bmw.osori.domain.signal.presentation.dto.request.SignalSampleCreateRequest;
import com.bmw.osori.domain.signal.presentation.dto.response.SignalSampleCreateResponse;
import com.bmw.osori.domain.signal.presentation.dto.response.SignalSampleListResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.GlobalExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SignalSampleController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ExtendWith(SpringExtension.class)
class SignalSampleControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SignalSampleService signalSampleService;

	@Test
	void 신호_샘플을_일괄_저장한다() throws Exception {
		SignalSampleCreateResponse response = new SignalSampleCreateResponse(125L, 2);
		given(signalSampleService.createSignalSamples(any(Long.class), any(SignalSampleCreateRequest.class)))
			.willReturn(response);

		mockMvc.perform(post("/api/v1/proximity-events/{eventId}/samples", 125L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"samples": [
							{
								"rssi": -72,
								"estimatedDistance": 8.1,
								"measuredAt": "2026-09-13T11:30:13.100"
							},
							{
								"rssi": -66,
								"estimatedDistance": 5.4,
								"measuredAt": "2026-09-13T11:30:13.300"
							}
						]
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(header().string(HttpHeaders.LOCATION, "/api/v1/proximity-events/125/samples"))
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_201"))
			.andExpect(jsonPath("$.message").value("요청이 성공적으로 생성되었습니다."))
			.andExpect(jsonPath("$.data.eventId").value(125))
			.andExpect(jsonPath("$.data.savedCount").value(2));
	}

	@Test
	void 존재하지_않는_차량_접근_이벤트에_신호_샘플_저장시_예외를_반환한다() throws Exception {
		willThrow(new BusinessException(ProximityEventErrorCode.PROXIMITY_EVENT_NOT_FOUND))
			.given(signalSampleService)
			.createSignalSamples(any(Long.class), any(SignalSampleCreateRequest.class));

		mockMvc.perform(post("/api/v1/proximity-events/{eventId}/samples", 999L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"samples": [
							{
								"rssi": -72,
								"estimatedDistance": 8.1,
								"measuredAt": "2026-09-13T11:30:13.100"
							}
						]
					}
					"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("PROXIMITY_EVENT_404"))
			.andExpect(jsonPath("$.message").value("차량 접근 이벤트를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void 신호_샘플_목록이_비어있으면_예외를_반환한다() throws Exception {
		mockMvc.perform(post("/api/v1/proximity-events/{eventId}/samples", 125L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"samples": []
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}

	@Test
	void 신호_샘플_요청값이_올바르지_않으면_예외를_반환한다() throws Exception {
		mockMvc.perform(post("/api/v1/proximity-events/{eventId}/samples", 125L)
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"samples": [
							{
								"rssi": null,
								"estimatedDistance": -1,
								"measuredAt": null
							}
						]
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}

	@Test
	void 신호_샘플_목록을_조회한다() throws Exception {
		SignalSampleListResponse response = new SignalSampleListResponse(
			125L,
			List.of(
				new SignalSampleListResponse.SignalSampleResponse(
					1L,
					-72,
					8.1,
					LocalDateTime.of(2026, 9, 13, 11, 30, 13, 100_000_000)
				),
				new SignalSampleListResponse.SignalSampleResponse(
					2L,
					-66,
					5.4,
					LocalDateTime.of(2026, 9, 13, 11, 30, 13, 300_000_000)
				)
			)
		);
		given(signalSampleService.getSignalSamples(125L))
			.willReturn(response);

		mockMvc.perform(get("/api/v1/proximity-events/{eventId}/samples", 125L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
			.andExpect(jsonPath("$.data.eventId").value(125))
			.andExpect(jsonPath("$.data.samples[0].sampleId").value(1))
			.andExpect(jsonPath("$.data.samples[0].rssi").value(-72))
			.andExpect(jsonPath("$.data.samples[0].estimatedDistance").value(8.1))
			.andExpect(jsonPath("$.data.samples[0].measuredAt").value("2026-09-13T11:30:13.1"))
			.andExpect(jsonPath("$.data.samples[1].sampleId").value(2))
			.andExpect(jsonPath("$.data.samples[1].rssi").value(-66))
			.andExpect(jsonPath("$.data.samples[1].estimatedDistance").value(5.4))
			.andExpect(jsonPath("$.data.samples[1].measuredAt").value("2026-09-13T11:30:13.3"));
	}

	@Test
	void 존재하지_않는_차량_접근_이벤트의_신호_샘플_조회시_예외를_반환한다() throws Exception {
		willThrow(new BusinessException(ProximityEventErrorCode.PROXIMITY_EVENT_NOT_FOUND))
			.given(signalSampleService)
			.getSignalSamples(999L);

		mockMvc.perform(get("/api/v1/proximity-events/{eventId}/samples", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("PROXIMITY_EVENT_404"))
			.andExpect(jsonPath("$.message").value("차량 접근 이벤트를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}
}
