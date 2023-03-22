package com.sample.project.security;

import com.sample.project.common.utils.CommonUtils;
import com.sample.project.common.wrapper.CommonErrorResponse;
import com.sample.project.config.http.filter.common.FilterUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.sample.project.common.type.ResponseErrorCode.FAIL_403;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
//        String queryString = request.getQueryString();
//        log.debug("Request : {} URI=[{}] Content-Type=[{}]",
//                request.getMethod(),
//                queryString == null ? request.getRequestURI() : request.getRequestURI() + queryString,
//                request.getContentType()
//        );
        FilterUtils.makeRequestlog(request);

        log.error(CommonUtils.getPrintStackTrace(accessDeniedException));
        CommonErrorResponse cer = new CommonErrorResponse(FAIL_403.code(), FAIL_403.message());
        String responseString = objectMapper.writeValueAsString(cer);

        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().append(responseString);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.getWriter().flush();
    }
}