package com.bmw.osori.domain.riskzone.presentation;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.riskzone.application.RiskZoneService;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.exception.RiskZoneErrorCode;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneDetailResponse;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = RiskZoneController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ExtendWith(SpringExtension.class)
class RiskZoneControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private RiskZoneService riskZoneService;

	@Test
	void 주변_위험구역_목록을_조회한다() throws Exception {
		RiskZoneListResponse response = new RiskZoneListResponse(
			List.of(new RiskZoneListResponse.RiskZoneResponse(
				1L,
				"어린이보호구역 인근 교차로",
				37.5665,
				126.978,
				100,
				87.5,
				RiskLevel.HIGH,
				12
			))
		);
		given(riskZoneService.getRiskZones(37.5665, 126.978, 500))
			.willReturn(response);

		mockMvc.perform(get("/api/v1/risk-zones")
				.param("latitude", "37.5665")
				.param("longitude", "126.978")
				.param("radius", "500"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
			.andExpect(jsonPath("$.data.content[0].riskZoneId").value(1))
			.andExpect(jsonPath("$.data.content[0].name").value("어린이보호구역 인근 교차로"))
			.andExpect(jsonPath("$.data.content[0].latitude").value(37.5665))
			.andExpect(jsonPath("$.data.content[0].longitude").value(126.978))
			.andExpect(jsonPath("$.data.content[0].radius").value(100))
			.andExpect(jsonPath("$.data.content[0].riskScore").value(87.5))
			.andExpect(jsonPath("$.data.content[0].riskLevel").value("HIGH"))
			.andExpect(jsonPath("$.data.content[0].accidentCount").value(12));
	}

	@Test
	void 주변_위험구역_목록_조회_파라미터가_올바르지_않으면_예외를_반환한다() throws Exception {
		mockMvc.perform(get("/api/v1/risk-zones")
				.param("latitude", "91")
				.param("longitude", "181")
				.param("radius", "0"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}

	@Test
	void 위험구역_상세를_조회한다() throws Exception {
		RiskZoneDetailResponse response = new RiskZoneDetailResponse(
			1L,
			"어린이보호구역 인근 교차로",
			37.5665,
			126.978,
			100,
			87.5,
			RiskLevel.HIGH,
			12,
			"risk-zone-v1.0.0",
			LocalDateTime.of(2026, 9, 13, 11, 30, 18)
		);
		given(riskZoneService.getRiskZone(1L))
			.willReturn(response);

		mockMvc.perform(get("/api/v1/risk-zones/{riskZoneId}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
			.andExpect(jsonPath("$.data.riskZoneId").value(1))
			.andExpect(jsonPath("$.data.name").value("어린이보호구역 인근 교차로"))
			.andExpect(jsonPath("$.data.latitude").value(37.5665))
			.andExpect(jsonPath("$.data.longitude").value(126.978))
			.andExpect(jsonPath("$.data.radius").value(100))
			.andExpect(jsonPath("$.data.riskScore").value(87.5))
			.andExpect(jsonPath("$.data.riskLevel").value("HIGH"))
			.andExpect(jsonPath("$.data.accidentCount").value(12))
			.andExpect(jsonPath("$.data.modelVersion").value("risk-zone-v1.0.0"))
			.andExpect(jsonPath("$.data.updatedAt").value("2026-09-13T11:30:18"));
	}

	@Test
	void 존재하지_않는_위험구역_상세_조회시_예외를_반환한다() throws Exception {
		willThrow(new BusinessException(RiskZoneErrorCode.RISK_ZONE_NOT_FOUND))
			.given(riskZoneService)
			.getRiskZone(999L);

		mockMvc.perform(get("/api/v1/risk-zones/{riskZoneId}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("RISK_ZONE_404"))
			.andExpect(jsonPath("$.message").value("위험구역을 찾을 수 없습니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}
}
