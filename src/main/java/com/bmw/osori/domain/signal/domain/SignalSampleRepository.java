package com.bmw.osori.domain.signal.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SignalSampleRepository extends JpaRepository<SignalSample, Long> {

	List<SignalSample> findAllByProximityEventIdOrderByMeasuredAtAsc(Long proximityEventId);
}
