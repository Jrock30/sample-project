package com.sample.project.config.http.filter.common;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class FilterUtils {

    public static void makeRequestlog(HttpServletRequest request) {
        String queryString = request.getQueryString();
        StringBuilder data = new StringBuilder();
        data.append("\n[========== Request ==========]")
                .append("\nmethod=[").append(request.getMethod()).append("]")
                .append("\nURI=[").append(queryString == null ? request.getRequestURI() : request.getRequestURI() + queryString).append("]")
                .append("\nContent-Type=[").append(request.getContentType()).append("]")
                .append("\nRemote-Address=[")
                .append(request.getRemoteAddr())
                .append("]");

        Map<String, String> reqHeaderMap = new HashMap<>();
        Enumeration<String> requestHeaderNameList = request.getHeaderNames();
        while (requestHeaderNameList.hasMoreElements()) {
            String headerName = requestHeaderNameList.nextElement();
            String headerValue = request.getHeader(headerName);
            reqHeaderMap.put(headerName, headerValue);
            data.append("\n").append(headerName).append("=[").append(headerValue).append("]");
        }

        log.debug(data.toString());
    }
}
