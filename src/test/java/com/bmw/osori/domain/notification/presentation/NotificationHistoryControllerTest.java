package com.bmw.osori.domain.notification.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.notification.application.NotificationHistoryService;
import com.bmw.osori.domain.notification.domain.NotificationStatus;
import com.bmw.osori.domain.notification.presentation.dto.request.NotificationHistoryCreateRequest;
import com.bmw.osori.domain.notification.presentation.dto.response.NotificationHistoryPageResponse;
import com.bmw.osori.domain.notification.presentation.dto.response.NotificationHistoryResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.GlobalExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = NotificationHistoryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class NotificationHistoryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private NotificationHistoryService notificationHistoryService;

	@Test
	void 알림_이력을_저장한다() throws Exception {
		given(notificationHistoryService.createNotificationHistory(any(NotificationHistoryCreateRequest.class)))
			.willReturn(response());

		mockMvc.perform(post("/api/v1/notification-histories")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
					  "deviceId": 1,
					  "proximityEventId": 125,
					  "notificationType": "PROXIMITY_ALERT",
					  "title": "차량 접근 경고",
					  "message": "근처에 차량이 접근하고 있습니다.",
					  "status": "SENT",
					  "sentAt": "2026-09-13T11:30:18"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.code").value("COMMON_201"))
			.andExpect(jsonPath("$.data.notificationHistoryId").value(1))
			.andExpect(jsonPath("$.data.proximityEventId").value(125))
			.andExpect(jsonPath("$.data.status").value("SENT"))
			.andExpect(jsonPath("$.data.sentAt").value("2026-09-13T11:30:18"));
	}

	@Test
	void 필수값이_없거나_잘못되면_400을_반환한다() throws Exception {
		mockMvc.perform(post("/api/v1/notification-histories")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{"deviceId": 0, "proximityEventId": null, "notificationType": "",
					 "title": "", "message": "", "status": "SENT"}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}

	@Test
	void 디바이스별_알림_이력을_페이지_조회한다() throws Exception {
		given(notificationHistoryService.getNotificationHistories(any(Long.class), any(Pageable.class)))
			.willReturn(new NotificationHistoryPageResponse(List.of(response()), 0, 20, 1L));

		mockMvc.perform(get("/api/v1/notification-histories").param("deviceId", "1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.content[0].notificationHistoryId").value(1))
			.andExpect(jsonPath("$.data.page").value(0))
			.andExpect(jsonPath("$.data.size").value(20))
			.andExpect(jsonPath("$.data.totalElements").value(1));
	}

	@Test
	void 잘못된_조회_파라미터는_400을_반환한다() throws Exception {
		mockMvc.perform(get("/api/v1/notification-histories")
				.param("deviceId", "0").param("page", "-1").param("size", "101"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}

	@Test
	void 없는_디바이스는_404를_반환한다() throws Exception {
		willThrow(new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND))
			.given(notificationHistoryService).getNotificationHistories(any(Long.class), any(Pageable.class));

		mockMvc.perform(get("/api/v1/notification-histories").param("deviceId", "999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("DEVICE_404"));
	}

	private NotificationHistoryResponse response() {
		return new NotificationHistoryResponse(
			1L, 1L, 125L, "PROXIMITY_ALERT", "차량 접근 경고",
			"근처에 차량이 접근하고 있습니다.", NotificationStatus.SENT,
			LocalDateTime.of(2026, 9, 13, 11, 30, 18),
			LocalDateTime.of(2026, 9, 13, 11, 30, 19)
		);
	}
}
