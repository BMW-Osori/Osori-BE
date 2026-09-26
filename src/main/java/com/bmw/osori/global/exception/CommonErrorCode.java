package com.bmw.osori.global.exception;

import org.springframework.http.HttpStatus;

public enum CommonErrorCode implements ErrorCode {

	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400", "잘못된 요청입니다."),
	INVALID_TYPE_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400_TYPE", "요청 값의 타입이 올바르지 않습니다."),
	MISSING_REQUIRED_VALUE(HttpStatus.BAD_REQUEST, "COMMON_400_MISSING", "필수 요청 값이 누락되었습니다."),
	UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON_401", "인증이 필요합니다."),
	FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON_403", "접근 권한이 없습니다."),
	NOT_FOUND(HttpStatus.NOT_FOUND, "COMMON_404", "요청한 리소스를 찾을 수 없습니다."),
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED, "COMMON_405", "지원하지 않는 HTTP 메서드입니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON_500", "서버 내부 오류가 발생했습니다.");

	private final HttpStatus status;
	private final String code;
	private final String message;

	CommonErrorCode(HttpStatus status, String code, String message) {
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
