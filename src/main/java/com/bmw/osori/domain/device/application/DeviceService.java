package com.bmw.osori.domain.device.application;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceRepository;
import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.device.presentation.dto.request.DeviceCreateRequest;
import com.bmw.osori.domain.device.presentation.dto.response.DeviceCreateResponse;
import com.bmw.osori.domain.device.presentation.dto.response.DeviceResponse;
import com.bmw.osori.global.exception.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class DeviceService {

	private final DeviceRepository deviceRepository;

	public DeviceService(DeviceRepository deviceRepository) {
		this.deviceRepository = deviceRepository;
	}

	@Transactional
	public DeviceCreateResponse createDevice(DeviceCreateRequest request) {
		validateDuplicateDeviceUuid(request.deviceUuid());

		Device device = Device.create(
			request.deviceUuid(),
			request.name(),
			request.deviceType()
		);
		Device savedDevice = deviceRepository.saveAndFlush(device);

		return DeviceCreateResponse.from(savedDevice);
	}

	public DeviceResponse getDevice(Long deviceId) {
		Device device = findDeviceById(deviceId);

		return DeviceResponse.from(device);
	}

	private Device findDeviceById(Long deviceId) {
		return deviceRepository.findById(deviceId)
			.orElseThrow(() -> new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND));
	}

	private void validateDuplicateDeviceUuid(String deviceUuid) {
		if (deviceRepository.existsByDeviceUuid(deviceUuid)) {
			throw new BusinessException(DeviceErrorCode.DUPLICATE_DEVICE_UUID);
		}
	}
}
