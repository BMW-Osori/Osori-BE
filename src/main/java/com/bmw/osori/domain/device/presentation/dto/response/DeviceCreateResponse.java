package com.bmw.osori.domain.device.presentation.dto.response;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceStatus;
import com.bmw.osori.domain.device.domain.DeviceType;
import java.time.LocalDateTime;

public record DeviceCreateResponse(
	Long deviceId,
	String deviceUuid,
	String name,
	DeviceType deviceType,
	DeviceStatus status,
	LocalDateTime createdAt
) {

	public static DeviceCreateResponse from(Device device) {
		return new DeviceCreateResponse(
			device.getId(),
			device.getDeviceUuid(),
			device.getName(),
			device.getDeviceType(),
			device.getStatus(),
			device.getCreatedAt()
		);
	}
}
