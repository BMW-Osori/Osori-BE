package com.bmw.osori.global.response;

public record ApiResponse<T>(
	boolean success,
	String code,
	String message,
	T data
) {

	public static <T> ApiResponse<T> ok(T data) {
		return from(SuccessCode.OK, data);
	}

	public static <T> ApiResponse<T> created(T data) {
		return from(SuccessCode.CREATED, data);
	}

	public static <T> ApiResponse<T> from(SuccessCode successCode, T data) {
		return new ApiResponse<>(true, successCode.getCode(), successCode.getMessage(), data);
	}

	public static ApiResponse<Void> ok() {
		return from(SuccessCode.OK, null);
	}

	public static ApiResponse<Void> fail(String code, String message) {
		return new ApiResponse<>(false, code, message, null);
	}
}
