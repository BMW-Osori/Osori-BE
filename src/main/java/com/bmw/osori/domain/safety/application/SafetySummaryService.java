package com.bmw.osori.domain.safety.application;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceRepository;
import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.riskzone.domain.RiskLevel;
import com.bmw.osori.domain.riskzone.domain.RiskZone;
import com.bmw.osori.domain.riskzone.domain.RiskZoneRepository;
import com.bmw.osori.domain.safety.presentation.dto.response.SafetySummaryResponse;
import com.bmw.osori.global.exception.BusinessException;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SafetySummaryService {

	private static final double EARTH_RADIUS_IN_METERS = 6_371_000;

	private final DeviceRepository deviceRepository;
	private final ProximityEventRepository proximityEventRepository;
	private final RiskZoneRepository riskZoneRepository;

	public SafetySummaryService(
		DeviceRepository deviceRepository,
		ProximityEventRepository proximityEventRepository,
		RiskZoneRepository riskZoneRepository
	) {
		this.deviceRepository = deviceRepository;
		this.proximityEventRepository = proximityEventRepository;
		this.riskZoneRepository = riskZoneRepository;
	}

	public SafetySummaryResponse getSafetySummary(
		Long deviceId,
		Double latitude,
		Double longitude,
		Integer radius
	) {
		Device device = findDeviceById(deviceId);
		ProximityEvent latestEvent = proximityEventRepository.findTopByDevice_IdOrderByStartedAtDescIdDesc(device.getId())
			.orElse(null);
		List<RiskZone> nearbyRiskZones = findNearbyRiskZones(latitude, longitude, radius);
		RiskLevel highestRiskLevel = findHighestRiskLevel(nearbyRiskZones);

		return SafetySummaryResponse.from(
			device.getId(),
			latestEvent,
			nearbyRiskZones,
			highestRiskLevel
		);
	}

	private Device findDeviceById(Long deviceId) {
		return deviceRepository.findById(deviceId)
			.orElseThrow(() -> new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND));
	}

	private List<RiskZone> findNearbyRiskZones(Double latitude, Double longitude, Integer radius) {
		return riskZoneRepository.findAll()
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
	}

	private RiskLevel findHighestRiskLevel(List<RiskZone> riskZones) {
		return riskZones.stream()
			.map(RiskZone::getRiskLevel)
			.max(Comparator.naturalOrder())
			.orElse(null);
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
