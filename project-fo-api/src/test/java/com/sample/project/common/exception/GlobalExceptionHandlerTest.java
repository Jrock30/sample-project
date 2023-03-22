package com.sample.project.common.exception;

import com.sample.project.api.login.dto.RequestLoginDto;
import com.sample.project.common.type.ResponseErrorCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GlobalExceptionHandlerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("CustomException 체크")
    @Test
    void handleCustomException() {
        assertThatThrownBy(() -> {
            throw new CustomException("커스텀 이셉션 테스트", HttpStatus.INTERNAL_SERVER_ERROR);
        })
                .isInstanceOf(CustomException.class)
                .hasMessageContaining("커스텀 이셉션 테스트");
    }

    @DisplayName("DefaultRuntimeException 체크")
    @Test
    void handleDefaultRuntimeException() {
        assertThatThrownBy(() -> {
            throw new DefaultRuntimeException(ResponseErrorCode.FAIL_4000, ResponseErrorCode.FAIL_4000.message());
        })
                .isInstanceOf(DefaultRuntimeException.class)
                .hasMessageContaining(ResponseErrorCode.FAIL_4000.message());

        assertThatThrownBy(() -> {
            throw new DefaultRuntimeException(ResponseErrorCode.FAIL_4000);
        })
                .isInstanceOf(DefaultRuntimeException.class)
                .hasMessageContaining(ResponseErrorCode.FAIL_4000.message());

        assertThatThrownBy(() -> {
            throw new DefaultRuntimeException(ResponseErrorCode.FAIL_4000.message());
        })
                .isInstanceOf(DefaultRuntimeException.class)
                .hasMessageContaining(ResponseErrorCode.FAIL_4000.message());

    }

    @DisplayName("DefaultException 체크")
    @Test
    void handleDefaultException() {
        assertThatThrownBy(() -> {
            throw new DefaultException(ResponseErrorCode.FAIL_4000, ResponseErrorCode.FAIL_4000.message());
        })
                .isInstanceOf(DefaultException.class)
                .hasMessageContaining(ResponseErrorCode.FAIL_4000.message());

        assertThatThrownBy(() -> {
            throw new DefaultException(ResponseErrorCode.FAIL_4000);
        })
                .isInstanceOf(DefaultException.class)
                .hasMessageContaining(ResponseErrorCode.FAIL_4000.message());

        assertThatThrownBy(() -> {
            throw new DefaultException(ResponseErrorCode.FAIL_4000.message());
        })
                .isInstanceOf(DefaultException.class)
                .hasMessageContaining(ResponseErrorCode.FAIL_4000.message());
    }

    @DisplayName("RequestValidateException 체크")
    @Test
    void handleRequestValidateException() throws Exception {
        RequestLoginDto requestLoginDto = new RequestLoginDto();
        requestLoginDto.setUserId("test1234");
        requestLoginDto.setPassword("test1234");

        Exception resolvedException = mockMvc.perform(post("/api/v1/login")
                        .contentType(MediaType.valueOf(MediaType.APPLICATION_JSON_VALUE))
                        .content(objectMapper.writeValueAsString(requestLoginDto)))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andReturn().getResolvedException();

        assertThat(resolvedException).isInstanceOf(RequestValidateException.class);

//        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
//        Validator validator = factory.getValidator();
//        Set<ConstraintViolation<RequestLoginDto>> constraintViolations = validator.validate(requestLoginDto);
//
//        assertThat(constraintViolations)
//                .extracting(ConstraintViolation::getMessage)
//                .contains("회원 ID를 입력해주세요.");

    }

}