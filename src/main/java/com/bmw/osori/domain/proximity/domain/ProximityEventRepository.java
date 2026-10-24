package com.bmw.osori.domain.proximity.domain;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProximityEventRepository extends
	JpaRepository<ProximityEvent, Long>,
	JpaSpecificationExecutor<ProximityEvent> {

	Optional<ProximityEvent> findTopByDevice_IdOrderByStartedAtDescIdDesc(Long deviceId);

	List<ProximityEvent> findByStartedAtBetweenOrderByStartedAtDescIdDesc(
		LocalDateTime from,
		LocalDateTime to
	);

	@Query("""
		select new com.bmw.osori.domain.proximity.domain.ProximityEventStatistics(
			e.alertLevel, count(e), sum(case when e.vibrationTriggered = true then 1L else 0L end)
		)
		from ProximityEvent e
		where e.device.id = :deviceId
			and (:from is null or e.startedAt >= :from)
			and (:to is null or e.startedAt <= :to)
		group by e.alertLevel
		""")
	List<ProximityEventStatistics> summarizeByDeviceAndPeriod(
		@Param("deviceId") Long deviceId,
		@Param("from") LocalDateTime from,
		@Param("to") LocalDateTime to
	);
}
