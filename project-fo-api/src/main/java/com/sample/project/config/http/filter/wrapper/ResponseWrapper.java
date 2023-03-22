package com.sample.project.config.http.filter.wrapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseWrapper extends ContentCachingResponseWrapper {
    private ObjectMapper objectMapper;
 
    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        this.objectMapper = new ObjectMapper();
    }

    public Object convertToObject() {
        try {
            return objectMapper.readValue(getContentAsByteArray(), Object.class);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public String getBody() {
        try {
            return objectMapper.writeValueAsString(convertToObject());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}