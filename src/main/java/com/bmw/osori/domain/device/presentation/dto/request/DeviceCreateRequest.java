package com.bmw.osori.domain.device.presentation.dto.request;

import com.bmw.osori.domain.device.domain.DeviceType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "디바이스 등록 요청")
public record DeviceCreateRequest(

	@Schema(description = "디바이스 고유 식별값", example = "BADGER-DEVICE-001")
	@NotBlank(message = "디바이스 고유 식별값은 필수입니다.")
	@Size(max = 100, message = "디바이스 고유 식별값은 100자 이하여야 합니다.")
	String deviceUuid,

	@Schema(description = "디바이스 이름", example = "전시용 수신기")
	@NotBlank(message = "디바이스 이름은 필수입니다.")
	@Size(max = 100, message = "디바이스 이름은 100자 이하여야 합니다.")
	String name,

	@Schema(description = "디바이스 형태", example = "WEARABLE")
	@NotNull(message = "디바이스 타입은 필수입니다.")
	DeviceType deviceType
) {
}
