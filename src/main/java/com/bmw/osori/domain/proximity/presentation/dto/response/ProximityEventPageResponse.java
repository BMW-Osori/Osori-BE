package com.bmw.osori.domain.proximity.presentation.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import org.springframework.data.domain.Page;

@Schema(description = "페이지 응답")
public record ProximityEventPageResponse<T>(
	@Schema(description = "현재 페이지 데이터")
	List<T> content,

	@Schema(description = "현재 페이지 번호", example = "0")
	Integer page,

	@Schema(description = "페이지 크기", example = "20")
	Integer size,

	@Schema(description = "전체 데이터 수", example = "1")
	Long totalElements
) {

	public static <T> ProximityEventPageResponse<T> from(Page<T> page) {
		return new ProximityEventPageResponse<>(
			page.getContent(),
			page.getNumber(),
			page.getSize(),
			page.getTotalElements()
		);
	}
}
