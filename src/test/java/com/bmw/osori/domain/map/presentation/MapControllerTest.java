package com.bmw.osori.domain.map.presentation;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.map.application.MapService;
import com.bmw.osori.domain.map.presentation.dto.response.MapResponse;
import com.bmw.osori.domain.map.presentation.dto.response.MapResponse.MapEventResponse;
import com.bmw.osori.domain.map.presentation.dto.response.MapResponse.MapRiskZoneResponse;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.global.exception.GlobalExceptionHandler;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = MapController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class MapControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private MapService mapService;

	@Test
	void 지도_통합_데이터를_조회한다() throws Exception {
		given(mapService.getMap(37.5665, 126.978, 1000)).willReturn(new MapResponse(
			List.of(new MapRiskZoneResponse(32L, 37.5672, 126.9785, RiskLevel.HIGH, 87.2)),
			List.of(new MapEventResponse(125L, 37.5665, 126.978, AlertLevel.DANGER,
				LocalDateTime.of(2026, 9, 13, 11, 30, 13)))
		));

		mockMvc.perform(get("/api/v1/map")
				.param("latitude", "37.5665")
				.param("longitude", "126.978")
				.param("radius", "1000"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.data.riskZones[0].id").value(32))
			.andExpect(jsonPath("$.data.riskZones[0].riskLevel").value("HIGH"))
			.andExpect(jsonPath("$.data.recentEvents[0].eventId").value(125))
			.andExpect(jsonPath("$.data.recentEvents[0].alertLevel").value("DANGER"))
			.andExpect(jsonPath("$.data.recentEvents[0].occurredAt").value("2026-09-13T11:30:13"));
	}

	@Test
	void 조회_결과가_없으면_빈_목록을_반환한다() throws Exception {
		given(mapService.getMap(37.5665, 126.978, 1000))
			.willReturn(new MapResponse(List.of(), List.of()));

		mockMvc.perform(get("/api/v1/map")
				.param("latitude", "37.5665")
				.param("longitude", "126.978")
				.param("radius", "1000"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.data.riskZones").isEmpty())
			.andExpect(jsonPath("$.data.recentEvents").isEmpty());
	}

	@Test
	void 조회_파라미터가_올바르지_않으면_예외를_반환한다() throws Exception {
		mockMvc.perform(get("/api/v1/map")
				.param("latitude", "91")
				.param("longitude", "181")
				.param("radius", "0"))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}
}
