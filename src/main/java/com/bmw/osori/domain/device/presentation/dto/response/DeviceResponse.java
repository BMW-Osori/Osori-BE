package com.bmw.osori.domain.device.presentation.dto.response;

import com.bmw.osori.domain.device.domain.Device;
import com.bmw.osori.domain.device.domain.DeviceStatus;
import com.bmw.osori.domain.device.domain.DeviceType;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "디바이스 조회 응답")
public record DeviceResponse(
	@Schema(description = "디바이스 ID", example = "1")
	Long deviceId,

	@Schema(description = "디바이스 고유 식별값", example = "BADGER-DEVICE-001")
	String deviceUuid,

	@Schema(description = "디바이스 이름", example = "전시용 수신기")
	String name,

	@Schema(description = "디바이스 형태", example = "WEARABLE")
	DeviceType deviceType,

	@Schema(description = "디바이스 활성화 상태", example = "ACTIVE")
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
