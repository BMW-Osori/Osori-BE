package com.bmw.osori.domain.map.application;

import com.bmw.osori.domain.map.presentation.dto.response.MapResponse;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.riskzone.application.RiskZoneService;
import com.bmw.osori.global.geo.GeoDistance;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class MapService {

	private static final int RECENT_EVENT_HOURS = 24;
	private static final int MAX_RECENT_EVENTS = 100;

	private final RiskZoneService riskZoneService;
	private final ProximityEventRepository proximityEventRepository;

	public MapService(RiskZoneService riskZoneService, ProximityEventRepository proximityEventRepository) {
		this.riskZoneService = riskZoneService;
		this.proximityEventRepository = proximityEventRepository;
	}

	public MapResponse getMap(Double latitude, Double longitude, Integer radius) {
		LocalDateTime now = LocalDateTime.now();
		List<ProximityEvent> events = proximityEventRepository
			.findByStartedAtBetweenOrderByStartedAtDescIdDesc(now.minusHours(RECENT_EVENT_HOURS), now)
			.stream()
			.filter(event -> GeoDistance.inMeters(
				latitude, longitude, event.getLatitude(), event.getLongitude()
			) <= radius)
			.limit(MAX_RECENT_EVENTS)
			.toList();

		return MapResponse.from(riskZoneService.getRiskZones(latitude, longitude, radius).riskZones(), events);
	}
}
