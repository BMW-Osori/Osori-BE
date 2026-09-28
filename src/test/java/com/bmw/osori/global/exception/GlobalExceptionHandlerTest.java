package com.bmw.osori.global.exception;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bmw.osori.global.response.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = GlobalExceptionHandlerTest.TestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({GlobalExceptionHandler.class, GlobalExceptionHandlerTest.TestController.class})
@ExtendWith(SpringExtension.class)
class GlobalExceptionHandlerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void 성공_응답을_공통_형식으로_반환한다() throws Exception {
		mockMvc.perform(get("/test/success"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.success").value(true))
			.andExpect(jsonPath("$.code").value("COMMON_200"))
			.andExpect(jsonPath("$.message").value("요청에 성공했습니다."))
			.andExpect(jsonPath("$.data.value").value("ok"));
	}

	@Test
	void 비즈니스_예외를_공통_에러_형식으로_반환한다() throws Exception {
		mockMvc.perform(get("/test/business-error"))
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_404"))
			.andExpect(jsonPath("$.message").value("테스트 리소스를 찾을 수 없습니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@Test
	void 요청값_검증_실패를_공통_에러_형식으로_반환한다() throws Exception {
		mockMvc.perform(post("/test/validation-error")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
					{
						"name": ""
					}
					"""))
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.success").value(false))
			.andExpect(jsonPath("$.code").value("COMMON_400"))
			.andExpect(jsonPath("$.message").value("name: 이름은 필수입니다."))
			.andExpect(jsonPath("$.data").doesNotExist());
	}

	@RestController
	@RequestMapping("/test")
	public static class TestController {

		@GetMapping("/success")
		ApiResponse<TestResponse> success() {
			return ApiResponse.ok(new TestResponse("ok"));
		}

		@GetMapping("/business-error")
		ApiResponse<Void> businessError() {
			throw new BusinessException(CommonErrorCode.NOT_FOUND, "테스트 리소스를 찾을 수 없습니다.");
		}

		@PostMapping("/validation-error")
		ApiResponse<Void> validationError(@Valid @RequestBody TestRequest request) {
			return ApiResponse.ok();
		}
	}

	record TestRequest(
		@NotBlank(message = "이름은 필수입니다.")
		String name
	) {
	}

	record TestResponse(String value) {
	}
}
