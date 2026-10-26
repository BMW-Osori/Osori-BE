package com.bmw.osori.domain.safety.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceRepository;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.domain.RiskZone;
import com.bmw.osori.domain.riskzone.domain.RiskZoneRepository;
import com.bmw.osori.domain.safety.presentation.dto.response.SafetySummaryResponse;
import com.bmw.osori.domain.safety.presentation.dto.response.SafetySummaryResponse.NearbyRiskZoneResponse;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class SafetySummaryServiceTest {

	private final DeviceRepository deviceRepository = mock(DeviceRepository.class);
	private final ProximityEventRepository proximityEventRepository = mock(ProximityEventRepository.class);
	private final RiskZoneRepository riskZoneRepository = mock(RiskZoneRepository.class);
	private final SafetySummaryService safetySummaryService = new SafetySummaryService(
		deviceRepository, proximityEventRepository, riskZoneRepository
	);

	@Test
	void 주변_위험구역만_가까운_순서로_반환하고_최고_위험_단계를_선택한다() {
		Device device = mock(Device.class);
		given(device.getId()).willReturn(1L);
		given(deviceRepository.findById(1L)).willReturn(Optional.of(device));
		given(proximityEventRepository.findTopByDevice_IdOrderByStartedAtDescIdDesc(1L))
			.willReturn(Optional.empty());
		RiskZone distant = riskZone(3L, 0.002, RiskLevel.HIGH);
		RiskZone nearby = riskZone(2L, 0.0005, RiskLevel.MEDIUM);
		RiskZone center = riskZone(1L, 0, RiskLevel.LOW);
		given(riskZoneRepository.findAll()).willReturn(List.of(distant, nearby, center));

		SafetySummaryResponse response = safetySummaryService.getSafetySummary(1L, 0.0, 0.0, 100);

		assertThat(response.nearbyRiskZoneCount()).isEqualTo(2);
		assertThat(response.nearbyRiskZones()).extracting(NearbyRiskZoneResponse::riskZoneId)
			.containsExactly(1L, 2L);
		assertThat(response.highestRiskLevel()).isEqualTo(RiskLevel.MEDIUM);
	}

	private RiskZone riskZone(Long id, double latitude, RiskLevel riskLevel) {
		RiskZone riskZone = mock(RiskZone.class);
		given(riskZone.getId()).willReturn(id);
		given(riskZone.getLatitude()).willReturn(latitude);
		given(riskZone.getLongitude()).willReturn(0.0);
		given(riskZone.getRiskLevel()).willReturn(riskLevel);
		return riskZone;
	}
}
