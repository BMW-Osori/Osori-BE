package com.bmw.osori.domain.riskzone.application;

import com.bmw.osori.domain.riskzone.domain.RiskZone;
import com.bmw.osori.domain.riskzone.domain.RiskZoneRepository;
import com.bmw.osori.domain.riskzone.exception.RiskZoneErrorCode;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneDetailResponse;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse;
import com.bmw.osori.domain.riskzone.presentation.dto.response.RiskZoneListResponse.RiskZoneResponse;
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
		List<RiskZoneResponse> riskZones = riskZoneRepository.findAll()
			.stream()
			.map(riskZone -> RiskZoneResponse.from(riskZone, GeoDistance.inMeters(
				latitude, longitude, riskZone.getLatitude(), riskZone.getLongitude()
			)))
			.filter(riskZone -> riskZone.distance() <= radius)
			.sorted(Comparator.comparingDouble(RiskZoneResponse::distance))
			.toList();

		return new RiskZoneListResponse(riskZones);
	}

	public RiskZoneDetailResponse getRiskZone(Long riskZoneId) {
		RiskZone riskZone = riskZoneRepository.findById(riskZoneId)
			.orElseThrow(() -> new BusinessException(RiskZoneErrorCode.RISK_ZONE_NOT_FOUND));

		return RiskZoneDetailResponse.from(riskZone);
	}
}
