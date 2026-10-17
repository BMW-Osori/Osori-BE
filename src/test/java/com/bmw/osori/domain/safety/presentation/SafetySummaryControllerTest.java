package com.bmw.osori.domain.safety.presentation;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.safety.application.SafetySummaryService;
import com.bmw.osori.domain.safety.presentation.dto.response.SafetySummaryResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.GlobalExceptionHandler;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SafetySummaryController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ExtendWith(SpringExtension.class)
class SafetySummaryControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SafetySummaryService safetySummaryService;

	@Test
	void 안전_요약을_조회한다() throws Exception {
		SafetySummaryResponse response = new SafetySummaryResponse(
			1L,
			AlertLevel.DANGER,
			125L,
			1,
			RiskLevel.HIGH,
			List.of(new SafetySummaryResponse.NearbyRiskZoneResponse(
				1L,
				"어린이보호구역 인근 교차로",
				RiskLevel.HIGH,
				87.5
			))
		);
		given(safetySummaryService.getSafetySummary(1L, 37.5665, 126.978, 500))
			.willReturn(response);

		mockMvc.perform(get("/api/v1/safety-summary")
				.param("deviceId", "1")
				.param("latitude", "37.5665")
				.param("longitude", "126.978")
				.param("radius", "500"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
			.andExpect(jsonPath("$.data.deviceId").value(1))
			.andExpect(jsonPath("$.data.latestAlertLevel").value("DANGER"))
			.andExpect(jsonPath("$.data.latestEventId").value(125))
			.andExpect(jsonPath("$.data.nearbyRiskZoneCount").value(1))
			.andExpect(jsonPath("$.data.highestRiskLevel").value("HIGH"))
			.andExpect(jsonPath("$.data.nearbyRiskZones[0].riskZoneId").value(1))
			.andExpect(jsonPath("$.data.nearbyRiskZones[0].name").value("어린이보호구역 인근 교차로"))
			.andExpect(jsonPath("$.data.nearbyRiskZones[0].riskLevel").value("HIGH"))
			.andExpect(jsonPath("$.data.nearbyRiskZones[0].riskScore").value(87.5));
	}

	@Test
	void 존재하지_않는_디바이스로_안전_요약_조회시_예외를_반환한다() throws Exception {
		willThrow(new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND))
			.given(safetySummaryService)
			.getSafetySummary(999L, 37.5665, 126.978, 500);

		mockMvc.perform(get("/api/v1/safety-summary")
				.param("deviceId", "999")
				.param("latitude", "37.5665")
				.param("longitude", "126.978")
				.param("radius", "500"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("DEVICE_404"))
			.andExpect(jsonPath("$.message").value("디바이스를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void 안전_요약_조회_파라미터가_올바르지_않으면_예외를_반환한다() throws Exception {
		mockMvc.perform(get("/api/v1/safety-summary")
				.param("deviceId", "1")
				.param("latitude", "91")
				.param("longitude", "181")
				.param("radius", "0"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}
}
