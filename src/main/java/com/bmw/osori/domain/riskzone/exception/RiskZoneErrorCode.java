package com.bmw.osori.domain.riskzone.exception;

import com.bmw.osori.global.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum RiskZoneErrorCode implements ErrorCode {

	RISK_ZONE_NOT_FOUND(HttpStatus.NOT_FOUND, "RISK_ZONE_404", "위험구역을 찾을 수 없습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	RiskZoneErrorCode(HttpStatus status, String code, String message) {
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
