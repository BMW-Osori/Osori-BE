package com.bmw.osori.domain.proximity.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.proximity.application.ProximityEventService;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.exception.ProximityEventErrorCode;
import com.bmw.osori.domain.proximity.presentation.dto.request.ProximityEventCreateRequest;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventCreateResponse;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventDetailResponse;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventListResponse;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventPageResponse;
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
import org.springframework.data.domain.Pageable;
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

	@Test
	void 차량_접근_이벤트_목록을_조회한다() throws Exception {
		ProximityEventListResponse item = new ProximityEventListResponse(
			125L,
			37.5665,
			126.978,
			AlertLevel.DANGER,
			2.8,
			true,
			LocalDateTime.of(2026, 9, 13, 11, 30, 13),
			LocalDateTime.of(2026, 9, 13, 11, 30, 18)
		);
		ProximityEventPageResponse<ProximityEventListResponse> response = new ProximityEventPageResponse<>(
			List.of(item),
			0,
			20,
			1L
		);
		given(proximityEventService.getProximityEvents(
			eq(1L),
			eq(LocalDateTime.of(2026, 9, 1, 0, 0)),
			eq(LocalDateTime.of(2026, 9, 30, 23, 59, 59)),
			eq(AlertLevel.DANGER),
			any(Pageable.class)
		)).willReturn(response);

		mockMvc.perform(get("/api/v1/proximity-events")
				.param("deviceId", "1")
				.param("from", "2026-09-01T00:00:00")
				.param("to", "2026-09-30T23:59:59")
				.param("alertLevel", "DANGER")
				.param("page", "0")
				.param("size", "20"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
			.andExpect(jsonPath("$.data.content[0].eventId").value(125))
			.andExpect(jsonPath("$.data.content[0].latitude").value(37.5665))
			.andExpect(jsonPath("$.data.content[0].longitude").value(126.978))
			.andExpect(jsonPath("$.data.content[0].alertLevel").value("DANGER"))
			.andExpect(jsonPath("$.data.content[0].minEstimatedDistance").value(2.8))
			.andExpect(jsonPath("$.data.content[0].vibrationTriggered").value(true))
			.andExpect(jsonPath("$.data.content[0].startedAt").value("2026-09-13T11:30:13"))
			.andExpect(jsonPath("$.data.content[0].endedAt").value("2026-09-13T11:30:18"))
			.andExpect(jsonPath("$.data.page").value(0))
			.andExpect(jsonPath("$.data.size").value(20))
			.andExpect(jsonPath("$.data.totalElements").value(1));
	}

	@Test
	void 차량_접근_이벤트_상세를_조회한다() throws Exception {
		ProximityEventDetailResponse response = new ProximityEventDetailResponse(
			125L,
			1L,
			37.5665,
			126.978,
			-53,
			2.8,
			AlertLevel.DANGER,
			5,
			true,
			LocalDateTime.of(2026, 9, 13, 11, 30, 13),
			LocalDateTime.of(2026, 9, 13, 11, 30, 18)
		);
		given(proximityEventService.getProximityEvent(125L))
			.willReturn(response);

		mockMvc.perform(get("/api/v1/proximity-events/{eventId}", 125L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
			.andExpect(jsonPath("$.data.eventId").value(125))
			.andExpect(jsonPath("$.data.deviceId").value(1))
			.andExpect(jsonPath("$.data.latitude").value(37.5665))
			.andExpect(jsonPath("$.data.longitude").value(126.978))
			.andExpect(jsonPath("$.data.maxRssi").value(-53))
			.andExpect(jsonPath("$.data.minEstimatedDistance").value(2.8))
			.andExpect(jsonPath("$.data.alertLevel").value("DANGER"))
			.andExpect(jsonPath("$.data.consecutiveCount").value(5))
			.andExpect(jsonPath("$.data.vibrationTriggered").value(true))
			.andExpect(jsonPath("$.data.startedAt").value("2026-09-13T11:30:13"))
			.andExpect(jsonPath("$.data.endedAt").value("2026-09-13T11:30:18"));
	}

	@Test
	void 존재하지_않는_차량_접근_이벤트_상세_조회시_예외를_반환한다() throws Exception {
		willThrow(new BusinessException(ProximityEventErrorCode.PROXIMITY_EVENT_NOT_FOUND))
			.given(proximityEventService)
			.getProximityEvent(999L);

		mockMvc.perform(get("/api/v1/proximity-events/{eventId}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("PROXIMITY_EVENT_404"))
			.andExpect(jsonPath("$.message").value("차량 접근 이벤트를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void 차량_접근_이벤트_목록_조회_파라미터가_올바르지_않으면_예외를_반환한다() throws Exception {
		mockMvc.perform(get("/api/v1/proximity-events")
				.param("deviceId", "invalid"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_400_TYPE"));
	}
}
