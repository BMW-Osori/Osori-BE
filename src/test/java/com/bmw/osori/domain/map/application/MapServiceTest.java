package com.bmw.osori.domain.map.application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.bmw.osori.domain.map.presentation.dto.response.MapResponse;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.riskzone.application.RiskZoneService;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse.RiskZoneResponse;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class MapServiceTest {

	private final RiskZoneService riskZoneService = mock(RiskZoneService.class);
	private final ProximityEventRepository proximityEventRepository = mock(ProximityEventRepository.class);
	private final MapService mapService = new MapService(riskZoneService, proximityEventRepository);

	@Test
	void 반경_내_위험구역과_최근_이벤트를_지도_응답으로_조회한다() {
		given(riskZoneService.getRiskZones(37.5665, 126.978, 100))
			.willReturn(new RiskZoneListResponse(List.of(
				new RiskZoneResponse(32L, "위험구역", 37.5665, 126.978, 50, 87.2, RiskLevel.HIGH, 3)
			)));
		List<ProximityEvent> events = List.of(
			event(125L, 37.5665, 126.978, AlertLevel.DANGER),
			event(124L, 37.568, 126.978, AlertLevel.CAUTION)
		);
		given(proximityEventRepository.findByStartedAtBetweenOrderByStartedAtDescIdDesc(any(), any()))
			.willReturn(events);

		LocalDateTime before = LocalDateTime.now();
		MapResponse response = mapService.getMap(37.5665, 126.978, 100);
		LocalDateTime after = LocalDateTime.now();

		assertThat(response.riskZones()).hasSize(1);
		assertThat(response.riskZones().getFirst().id()).isEqualTo(32L);
		assertThat(response.riskZones().getFirst().riskLevel()).isEqualTo(RiskLevel.HIGH);
		assertThat(response.recentEvents()).extracting(MapResponse.MapEventResponse::eventId)
			.containsExactly(125L);
		assertThat(response.recentEvents().getFirst().alertLevel()).isEqualTo(AlertLevel.DANGER);

		ArgumentCaptor<LocalDateTime> from = ArgumentCaptor.forClass(LocalDateTime.class);
		ArgumentCaptor<LocalDateTime> to = ArgumentCaptor.forClass(LocalDateTime.class);
		verify(proximityEventRepository).findByStartedAtBetweenOrderByStartedAtDescIdDesc(from.capture(), to.capture());
		assertThat(from.getValue()).isBetween(before.minusHours(24), after.minusHours(24));
		assertThat(to.getValue()).isBetween(before, after);
	}

	@Test
	void 반경_내_이벤트를_최대_100건까지_반환한다() {
		given(riskZoneService.getRiskZones(37.5665, 126.978, 100))
			.willReturn(new RiskZoneListResponse(List.of()));
		List<ProximityEvent> events = new ArrayList<>();
		for (long id = 101; id >= 1; id--) {
			events.add(event(id, 37.5665, 126.978, AlertLevel.CAUTION));
		}
		given(proximityEventRepository.findByStartedAtBetweenOrderByStartedAtDescIdDesc(any(), any()))
			.willReturn(events);

		MapResponse response = mapService.getMap(37.5665, 126.978, 100);

		assertThat(response.riskZones()).isEmpty();
		assertThat(response.recentEvents()).hasSize(100);
		assertThat(response.recentEvents().getFirst().eventId()).isEqualTo(101L);
		assertThat(response.recentEvents().getLast().eventId()).isEqualTo(2L);
	}

	@Test
	void 조회_결과가_없으면_빈_목록을_반환한다() {
		given(riskZoneService.getRiskZones(37.5665, 126.978, 100))
			.willReturn(new RiskZoneListResponse(List.of()));
		given(proximityEventRepository.findByStartedAtBetweenOrderByStartedAtDescIdDesc(any(), any()))
			.willReturn(List.of());

		MapResponse response = mapService.getMap(37.5665, 126.978, 100);

		assertThat(response.riskZones()).isEmpty();
		assertThat(response.recentEvents()).isEmpty();
	}

	private ProximityEvent event(Long id, Double latitude, Double longitude, AlertLevel alertLevel) {
		ProximityEvent event = mock(ProximityEvent.class);
		given(event.getId()).willReturn(id);
		given(event.getLatitude()).willReturn(latitude);
		given(event.getLongitude()).willReturn(longitude);
		given(event.getAlertLevel()).willReturn(alertLevel);
		given(event.getStartedAt()).willReturn(LocalDateTime.of(2026, 9, 13, 11, 30, 13));
		return event;
	}
}
