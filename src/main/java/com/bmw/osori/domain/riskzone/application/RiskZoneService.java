package com.bmw.osori.domain.riskzone.application;

import com.bmw.osori.domain.riskzone.domain.RiskZone;
import com.bmw.osori.domain.riskzone.domain.RiskZoneRepository;
import com.bmw.osori.domain.riskzone.exception.RiskZoneErrorCode;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneDetailResponse;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.geo.GeoDistance;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RiskZoneService {

	private final RiskZoneRepository riskZoneRepository;

	public RiskZoneService(RiskZoneRepository riskZoneRepository) {
		this.riskZoneRepository = riskZoneRepository;
	}

	public RiskZoneListResponse getRiskZones(Double latitude, Double longitude, Integer radius) {
		List<RiskZone> riskZones = riskZoneRepository.findAll()
			.stream()
			.filter(riskZone -> GeoDistance.inMeters(
				latitude,
				longitude,
				riskZone.getLatitude(),
				riskZone.getLongitude()
			) <= radius)
			.sorted(Comparator.comparingDouble(riskZone -> GeoDistance.inMeters(
				latitude,
				longitude,
				riskZone.getLatitude(),
				riskZone.getLongitude()
			)))
			.toList();

		return RiskZoneListResponse.from(riskZones);
	}

	public RiskZoneDetailResponse getRiskZone(Long riskZoneId) {
		RiskZone riskZone = riskZoneRepository.findById(riskZoneId)
			.orElseThrow(() -> new BusinessException(RiskZoneErrorCode.RISK_ZONE_NOT_FOUND));

		return RiskZoneDetailResponse.from(riskZone);
	}
}
