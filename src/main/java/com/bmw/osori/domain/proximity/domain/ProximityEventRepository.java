package com.bmw.osori.domain.proximity.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ProximityEventRepository extends
	JpaRepository<ProximityEvent, Long>,
	JpaSpecificationExecutor<ProximityEvent> {

	Optional<ProximityEvent> findTopByDevice_IdOrderByStartedAtDescIdDesc(Long deviceId);

	List<ProximityEvent> findByStartedAtBetweenOrderByStartedAtDescIdDesc(
		LocalDateTime from,
		LocalDateTime to
	);
}
