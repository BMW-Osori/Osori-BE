package com.bmw.osori.domain.proximity.domain;

import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;

public final class ProximityEventSpecification {

	private ProximityEventSpecification() {
	}

	public static Specification<ProximityEvent> deviceIdEquals(Long deviceId) {
		return (root, query, criteriaBuilder) -> deviceId == null
			? null
			: criteriaBuilder.equal(root.get("device").get("id"), deviceId);
	}

	public static Specification<ProximityEvent> startedAtGreaterThanOrEqualTo(LocalDateTime from) {
		return (root, query, criteriaBuilder) -> from == null
			? null
			: criteriaBuilder.greaterThanOrEqualTo(root.get("startedAt"), from);
	}

	public static Specification<ProximityEvent> startedAtLessThanOrEqualTo(LocalDateTime to) {
		return (root, query, criteriaBuilder) -> to == null
			? null
			: criteriaBuilder.lessThanOrEqualTo(root.get("startedAt"), to);
	}

	public static Specification<ProximityEvent> alertLevelEquals(AlertLevel alertLevel) {
		return (root, query, criteriaBuilder) -> alertLevel == null
			? null
			: criteriaBuilder.equal(root.get("alertLevel"), alertLevel);
	}
}
