package com.bmw.osori.domain.riskzone.application;

import com.bmw.osori.domain.riskzone.domain.RiskZone;
import com.bmw.osori.domain.riskzone.domain.RiskZoneRepository;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RiskZoneService {

	private static final double EARTH_RADIUS_IN_METERS = 6_371_000;

	private final RiskZoneRepository riskZoneRepository;

	public RiskZoneService(RiskZoneRepository riskZoneRepository) {
		this.riskZoneRepository = riskZoneRepository;
	}

	public RiskZoneListResponse getRiskZones(Double latitude, Double longitude, Integer radius) {
		List<RiskZone> riskZones = riskZoneRepository.findAll()
			.stream()
			.filter(riskZone -> calculateDistanceInMeters(
				latitude,
				longitude,
				riskZone.getLatitude(),
				riskZone.getLongitude()
			) <= radius)
			.sorted(Comparator.comparingDouble(riskZone -> calculateDistanceInMeters(
				latitude,
				longitude,
				riskZone.getLatitude(),
				riskZone.getLongitude()
			)))
			.toList();

		return RiskZoneListResponse.from(riskZones);
	}

	private double calculateDistanceInMeters(
		double sourceLatitude,
		double sourceLongitude,
		double targetLatitude,
		double targetLongitude
	) {
		double sourceLatitudeRadians = Math.toRadians(sourceLatitude);
		double targetLatitudeRadians = Math.toRadians(targetLatitude);
		double latitudeDifference = Math.toRadians(targetLatitude - sourceLatitude);
		double longitudeDifference = Math.toRadians(targetLongitude - sourceLongitude);

		double haversine = Math.sin(latitudeDifference / 2) * Math.sin(latitudeDifference / 2)
			+ Math.cos(sourceLatitudeRadians) * Math.cos(targetLatitudeRadians)
			* Math.sin(longitudeDifference / 2) * Math.sin(longitudeDifference / 2);
		double angularDistance = 2 * Math.atan2(Math.sqrt(haversine), Math.sqrt(1 - haversine));

		return EARTH_RADIUS_IN_METERS * angularDistance;
	}
}
