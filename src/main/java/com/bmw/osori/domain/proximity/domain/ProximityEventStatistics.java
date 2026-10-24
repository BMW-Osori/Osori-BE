package com.bmw.osori.domain.proximity.domain;

public record ProximityEventStatistics(
	AlertLevel alertLevel,
	Long eventCount,
	Long vibrationCount
) {
}
