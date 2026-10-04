package com.bmw.osori.domain.proximity.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.proximity.application.ProximityEventService;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.presentation.dto.request.ProximityEventCreateRequest;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventCreateResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.GlobalExceptionHandler;
import java.time.LocalDateTime;
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

@WebMvcTest(controllers = ProximityEventController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ExtendWith(SpringExtension.class)
class ProximityEventControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private ProximityEventService proximityEventService;

	@Test
	void 차량_접근_이벤트를_저장한다() throws Exception {
		ProximityEventCreateResponse response = new ProximityEventCreateResponse(
			125L,
			AlertLevel.DANGER,
			true,
			LocalDateTime.of(2026, 9, 13, 11, 30, 18)
		);
		given(proximityEventService.createProximityEvent(any(ProximityEventCreateRequest.class)))
			.willReturn(response);

		mockMvc.perform(post("/api/v1/proximity-events")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"deviceId": 1,
						"latitude": 37.5665,
						"longitude": 126.978,
						"maxRssi": -53,
						"minEstimatedDistance": 2.8,
						"alertLevel": "DANGER",
						"consecutiveCount": 5,
						"vibrationTriggered": true,
						"startedAt": "2026-09-13T11:30:13",
						"endedAt": "2026-09-13T11:30:18"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(header().string(HttpHeaders.LOCATION, "/api/v1/proximity-events/125"))
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_201"))
			.andExpect(jsonPath("$.message").value("요청이 성공적으로 생성되었습니다."))
			.andExpect(jsonPath("$.data.eventId").value(125))
			.andExpect(jsonPath("$.data.alertLevel").value("DANGER"))
			.andExpect(jsonPath("$.data.vibrationTriggered").value(true))
			.andExpect(jsonPath("$.data.createdAt").value("2026-09-13T11:30:18"));
	}

	@Test
	void 존재하지_않는_디바이스로_차량_접근_이벤트_저장시_예외를_반환한다() throws Exception {
		willThrow(new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND))
			.given(proximityEventService)
			.createProximityEvent(any(ProximityEventCreateRequest.class));

		mockMvc.perform(post("/api/v1/proximity-events")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"deviceId": 999,
						"latitude": 37.5665,
						"longitude": 126.978,
						"maxRssi": -53,
						"minEstimatedDistance": 2.8,
						"alertLevel": "DANGER",
						"consecutiveCount": 5,
						"vibrationTriggered": true,
						"startedAt": "2026-09-13T11:30:13",
						"endedAt": "2026-09-13T11:30:18"
					}
					"""))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("DEVICE_404"))
			.andExpect(jsonPath("$.message").value("디바이스를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void 차량_접근_이벤트_저장_요청값이_올바르지_않으면_예외를_반환한다() throws Exception {
		mockMvc.perform(post("/api/v1/proximity-events")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"deviceId": null,
						"latitude": 91,
						"longitude": 181,
						"maxRssi": null,
						"minEstimatedDistance": -1,
						"alertLevel": null,
						"consecutiveCount": -1,
						"vibrationTriggered": null,
						"startedAt": null
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}
}
