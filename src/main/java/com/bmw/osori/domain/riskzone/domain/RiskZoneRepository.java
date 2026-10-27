package com.bmw.osori.domain.riskzone.domain;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskZoneRepository extends JpaRepository<RiskZone, Long> {

	Optional<RiskZone> findBySourceKey(String sourceKey);
}
