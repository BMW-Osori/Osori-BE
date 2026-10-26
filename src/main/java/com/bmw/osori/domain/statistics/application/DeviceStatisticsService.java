package com.bmw.osori.domain.statistics.application;

import com.bmw.osori.domain.device.domain.DeviceRepository;
import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.proximity.domain.AlertLevel;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.proximity.domain.ProximityEventStatistics;
import com.bmw.osori.domain.statistics.presentation.dto.response.DeviceStatisticsResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.CommonErrorCode;
import java.time.LocalDateTime;
import java.util.EnumMap;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DeviceStatisticsService {

	private final DeviceRepository deviceRepository;
	private final ProximityEventRepository proximityEventRepository;

	public DeviceStatisticsService(DeviceRepository deviceRepository, ProximityEventRepository proximityEventRepository) {
		this.deviceRepository = deviceRepository;
		this.proximityEventRepository = proximityEventRepository;
	}

	public DeviceStatisticsResponse getDeviceStatistics(Long deviceId, LocalDateTime from, LocalDateTime to) {
		if (from != null && to != null && from.isAfter(to)) {
			throw new BusinessException(CommonErrorCode.INVALID_INPUT_VALUE, "조회 시작 시각은 종료 시각보다 늦을 수 없습니다.");
		}
		if (!deviceRepository.existsById(deviceId)) {
			throw new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND);
		}

		List<ProximityEventStatistics> statistics = proximityEventRepository.summarizeByDeviceAndPeriod(deviceId, from, to);
		EnumMap<AlertLevel, Long> counts = new EnumMap<>(AlertLevel.class);
		for (AlertLevel level : AlertLevel.values()) {
			counts.put(level, 0L);
		}
		long totalCount = 0;
		long vibrationTriggeredCount = 0;
		for (ProximityEventStatistics entry : statistics) {
			counts.put(entry.alertLevel(), entry.eventCount());
			totalCount += entry.eventCount();
			vibrationTriggeredCount += entry.vibrationCount();
		}

		return new DeviceStatisticsResponse(
			deviceId, from, to, totalCount, counts, vibrationTriggeredCount
		);
	}
}
