package com.bmw.osori.domain.device.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.domain.device.application.DeviceService;
import com.bmw.osori.domain.device.domain.DeviceStatus;
import com.bmw.osori.domain.device.domain.DeviceType;
import com.bmw.osori.domain.device.exception.DeviceErrorCode;
import com.bmw.osori.domain.device.presentation.dto.request.DeviceCreateRequest;
import com.bmw.osori.domain.device.presentation.dto.response.DeviceCreateResponse;
import com.bmw.osori.domain.device.presentation.dto.response.DeviceResponse;
import com.bmw.osori.global.exception.BusinessException;
import com.bmw.osori.global.exception.GlobalExceptionHandler;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DeviceController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@ExtendWith(SpringExtension.class)
class DeviceControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private DeviceService deviceService;

	@Test
	void 디바이스를_등록한다() throws Exception {
		DeviceCreateResponse response = new DeviceCreateResponse(
			1L,
			"BADGER-DEVICE-001",
			"전시용 수신기",
			DeviceType.WEARABLE,
			DeviceStatus.ACTIVE,
			LocalDateTime.of(2026, 9, 29, 8, 0)
		);
		given(deviceService.createDevice(any(DeviceCreateRequest.class)))
			.willReturn(response);

		mockMvc.perform(post("/api/v1/devices")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"deviceUuid": "BADGER-DEVICE-001",
						"name": "전시용 수신기",
						"deviceType": "WEARABLE"
					}
					"""))
			.andExpect(status().isCreated())
			.andExpect(header().string(HttpHeaders.LOCATION, "/api/v1/devices/1"))
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_201"))
			.andExpect(jsonPath("$.message").value("요청이 성공적으로 생성되었습니다."))
			.andExpect(jsonPath("$.data.deviceId").value(1))
			.andExpect(jsonPath("$.data.deviceUuid").value("BADGER-DEVICE-001"))
			.andExpect(jsonPath("$.data.name").value("전시용 수신기"))
			.andExpect(jsonPath("$.data.deviceType").value("WEARABLE"))
			.andExpect(jsonPath("$.data.status").value("ACTIVE"))
			.andExpect(jsonPath("$.data.createdAt").value("2026-09-29T08:00:00"));
	}

	@Test
	void 디바이스를_단건_조회한다() throws Exception {
		DeviceResponse response = new DeviceResponse(
			1L,
			"BADGER-DEVICE-001",
			"전시용 수신기",
			DeviceType.WEARABLE,
			DeviceStatus.ACTIVE
		);
		given(deviceService.getDevice(1L))
			.willReturn(response);

		mockMvc.perform(get("/api/v1/devices/{deviceId}", 1L))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
			.andExpect(jsonPath("$.data.deviceId").value(1))
			.andExpect(jsonPath("$.data.deviceUuid").value("BADGER-DEVICE-001"))
			.andExpect(jsonPath("$.data.name").value("전시용 수신기"))
			.andExpect(jsonPath("$.data.deviceType").value("WEARABLE"))
			.andExpect(jsonPath("$.data.status").value("ACTIVE"));
	}

	@Test
	void 존재하지_않는_디바이스_조회시_예외를_반환한다() throws Exception {
		willThrow(new BusinessException(DeviceErrorCode.DEVICE_NOT_FOUND))
			.given(deviceService)
			.getDevice(999L);

		mockMvc.perform(get("/api/v1/devices/{deviceId}", 999L))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("DEVICE_404"))
			.andExpect(jsonPath("$.message").value("디바이스를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void 디바이스_등록_요청값이_올바르지_않으면_예외를_반환한다() throws Exception {
		mockMvc.perform(post("/api/v1/devices")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"deviceUuid": "",
						"name": "",
						"deviceType": null
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_400"));
	}
}
