package com.bmw.osori.domain.riskzone.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.data.Offset.offset;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.domain.RiskZone;
import com.bmw.osori.domain.riskzone.domain.RiskZoneRepository;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse.RiskZoneResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class RiskZoneServiceTest {

	private final RiskZoneRepository riskZoneRepository = mock(RiskZoneRepository.class);
	private final RiskZoneService riskZoneService = new RiskZoneService(riskZoneRepository);

	@Test
	void 반경_내_위험구역을_가까운_순서대로_조회한다() {
		RiskZone distant = riskZone(3L, 0.002);
		RiskZone nearby = riskZone(2L, 0.0005);
		RiskZone center = riskZone(1L, 0);
		given(riskZoneRepository.findAll()).willReturn(List.of(distant, nearby, center));

		RiskZoneListResponse response = riskZoneService.getRiskZones(0.0, 0.0, 100);

		assertThat(response.riskZones()).extracting(RiskZoneResponse::riskZoneId)
			.containsExactly(1L, 2L);
		assertThat(response.riskZones().get(0).distance()).isZero();
		assertThat(response.riskZones().get(1).distance()).isCloseTo(55.6, offset(0.1));
	}

	@Test
	void 검색_반경의_경계에_있는_위험구역도_포함한다() {
		RiskZone center = riskZone(1L, 0);
		RiskZone nearby = riskZone(2L, 0.0005);
		given(riskZoneRepository.findAll()).willReturn(List.of(nearby, center));

		RiskZoneListResponse response = riskZoneService.getRiskZones(0.0, 0.0, 0);

		assertThat(response.riskZones()).extracting(RiskZoneResponse::riskZoneId)
			.containsExactly(1L);
		assertThat(response.riskZones().getFirst().distance()).isZero();
	}

	private RiskZone riskZone(Long id, double latitude) {
		RiskZone riskZone = mock(RiskZone.class);
		given(riskZone.getId()).willReturn(id);
		given(riskZone.getLatitude()).willReturn(latitude);
		given(riskZone.getLongitude()).willReturn(0.0);
		given(riskZone.getRiskLevel()).willReturn(RiskLevel.LOW);
		return riskZone;
	}
}
