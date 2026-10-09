package com.bmw.osori.domain.proximity.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProximityEventRepository extends
	JpaRepository<ProximityEvent, Long>,
	JpaSpecificationExecutor<ProximityEvent> {
}
