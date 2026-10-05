package com.bmw.osori.domain.proximity.application;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceRepository;
import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.proximity.domain.ProximityEvent;
import com.bmw.osori.domain.proximity.domain.ProximityEventRepository;
import com.bmw.osori.domain.proximity.presentation.dto.request.ProximityEventCreateRequest;
import com.bmw.osori.domain.proximity.presentation.dto.response.ProximityEventCreateResponse;
import com.bmw.osori.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ProximityEventService {

	private final ProximityEventRepository proximityEventRepository;
	private final DeviceRepository deviceRepository;

	public ProximityEventService(
		ProximityEventRepository proximityEventRepository,
		DeviceRepository deviceRepository
	) {
		this.proximityEventRepository = proximityEventRepository;
		this.deviceRepository = deviceRepository;
	}

	@Transactional
	public ProximityEventCreateResponse createProximityEvent(ProximityEventCreateRequest request) {
		Device device = findDeviceById(request.deviceId());
		ProximityEvent proximityEvent = ProximityEvent.create(
			device,
			request.latitude(),
			request.longitude(),
			request.maxRssi(),
			request.minEstimatedDistance(),
			request.alertLevel(),
			request.consecutiveCount(),
			request.vibrationTriggered(),
			request.startedAt(),
			request.endedAt()
		);
		ProximityEvent savedProximityEvent = proximityEventRepository.saveAndFlush(proximityEvent);

		return ProximityEventCreateResponse.from(savedProximityEvent);
	}

	private Device findDeviceById(Long deviceId) {
		return deviceRepository.findById(deviceId)
			.orElseThrow(() -> new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND));
	}
}
