package com.bmw.osori.domain.proximity.exception;

import com.bmw.osori.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum ProximityEventErrorCode implements ErrorCode {

	PROXIMITY_EVENT_NOT_FOUND(HttpStatus.NOT_FOUND, "PROXIMITY_EVENT_404", "차량 접근 이벤트를 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	ProximityEventErrorCode(HttpStatus status, String code, String message) {
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
