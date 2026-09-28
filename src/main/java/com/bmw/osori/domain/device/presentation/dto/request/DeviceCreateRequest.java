package com.bmw.osori.domain.device.presentation.dto.request;

import com.bmw.osori.domain.device.domain.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DeviceCreateRequest(

	@NotBlank(message = "디바이스 고유 식별값은 필수입니다.")
	@Size(max = 100, message = "디바이스 고유 식별값은 100자 이하여야 합니다.")
	String deviceUuid,

	@NotBlank(message = "디바이스 이름은 필수입니다.")
	@Size(max = 100, message = "디바이스 이름은 100자 이하여야 합니다.")
	String name,

	@NotNull(message = "디바이스 타입은 필수입니다.")
	DeviceType deviceType
) {
}
