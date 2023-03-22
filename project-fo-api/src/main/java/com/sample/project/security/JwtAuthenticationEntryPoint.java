package com.sample.project.security;

import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.wrapper.CommonErrorResponse;
import com.sample.project.config.http.filter.common.FilterUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.sample.project.common.type.ResponseErrorCode.FAIL_401;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        FilterUtils.makeRequestlog(request);

        log.error(CommonUtils.getPrintStackTrace(authException));
        CommonErrorResponse cer = new CommonErrorResponse(FAIL_401.code(), FAIL_401.message());
        String responseString = objectMapper.writeValueAsString(cer);

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().append(responseString);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().flush();
    }
}