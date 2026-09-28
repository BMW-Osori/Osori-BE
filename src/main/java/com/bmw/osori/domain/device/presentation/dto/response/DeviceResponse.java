package com.bmw.osori.domain.device.presentation.dto.response;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceStatus;
import com.bmw.osori.domain.device.domain.DeviceType;

public record DeviceResponse(
	Long deviceId,
	String deviceUuid,
	String name,
	DeviceType deviceType,
	DeviceStatus status
) {

	public static DeviceResponse from(Device device) {
		return new DeviceResponse(
			device.getId(),
			device.getDeviceUuid(),
			device.getName(),
			device.getDeviceType(),
			device.getStatus()
		);
	}
}
