package com.auth_example.auth_service.jwt;

import com.auth_example.common_service.core.responses.ApiError;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static com.auth_example.common_service.core.responses.ApiErrorCode.ENTITY_ALREADY_EXIST;
import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        ApiError error = new ApiError(ENTITY_ALREADY_EXIST, accessDeniedException.getMessage());

        response.setContentType("application/json");
        response.setStatus(SC_FORBIDDEN);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(ApiResponse.error(error)));
    }
}
