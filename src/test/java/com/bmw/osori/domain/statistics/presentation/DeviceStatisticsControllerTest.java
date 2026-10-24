package com.bmw.osori.domain.statistics.presentation;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.statistics.application.DeviceStatisticsService;
import com.bmw.osori.domain.statistics.presentation.dto.response.DeviceStatisticsResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.GlobalExceptionHandler;
import java.time.LocalDateTime;
import java.util.EnumMap;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DeviceStatisticsController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class DeviceStatisticsControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DeviceStatisticsService deviceStatisticsService;

	@Test
	void 디바이스_감지_통계를_조회한다() throws Exception {
		LocalDateTime from = LocalDateTime.of(2026, 9, 1, 0, 0);
		LocalDateTime to = LocalDateTime.of(2026, 9, 30, 23, 59, 59);
		EnumMap<AlertLevel, Long> counts = new EnumMap<>(AlertLevel.class);
		counts.put(AlertLevel.SAFE, 0L);
		counts.put(AlertLevel.CAUTION, 2L);
		counts.put(AlertLevel.DANGER, 3L);
		counts.put(AlertLevel.RELEASED, 0L);
		given(deviceStatisticsService.getDeviceStatistics(1L, from, to))
			.willReturn(new DeviceStatisticsResponse(1L, from, to, 5L, counts, 3L));

		mockMvc.perform(get("/api/v1/statistics/devices/1")
				.param("from", "2026-09-01T00:00:00")
				.param("to", "2026-09-30T23:59:59"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.data.deviceId").value(1))
			.andExpect(jsonPath("$.data.totalCount").value(5))
			.andExpect(jsonPath("$.data.alertLevelCounts.CAUTION").value(2))
			.andExpect(jsonPath("$.data.alertLevelCounts.DANGER").value(3))
			.andExpect(jsonPath("$.data.vibrationTriggeredCount").value(3));
	}

	@Test
	void 존재하지_않는_디바이스는_404를_반환한다() throws Exception {
		willThrow(new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND))
			.given(deviceStatisticsService).getDeviceStatistics(999L, null, null);

		mockMvc.perform(get("/api/v1/statistics/devices/999"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.code").value("DEVICE_404"));
	}

	@Test
	void 잘못된_디바이스_ID와_기간_형식은_400을_반환한다() throws Exception {
		mockMvc.perform(get("/api/v1/statistics/devices/0"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON_400"));

		mockMvc.perform(get("/api/v1/statistics/devices/1").param("from", "invalid"))
			.andExpect(status().isBadRequest());
	}
}
