package com.bmw.osori.domain.device.exception;

import com.bmw.osori.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum DeviceErrorCode implements ErrorCode {

	DEVICE_NOT_FOUND(HttpStatus.NOT_FOUND, "DEVICE_404", "디바이스를 찾을 수 없습니다."),
	DUPLICATE_DEVICE_UUID(HttpStatus.CONFLICT, "DEVICE_409", "이미 등록된 디바이스입니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	DeviceErrorCode(HttpStatus status, String code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}

	@Override
	public HttpStatus getStatus() {
		return status;
	}

	@Override
	public String getCode() {
		return code;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
