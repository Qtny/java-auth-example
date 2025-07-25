package com.auth_example.auth_service.jwt;

import com.auth_example.common_service.core.responses.ApiError;
import com.auth_example.common_service.core.responses.ApiErrorCode;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.auth_example.common_service.core.responses.ApiErrorCode.UNAUTHORIZED;
import static jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        ApiError error = new ApiError(UNAUTHORIZED, authException.getMessage());

        response.setContentType("application/json");
        response.setStatus(SC_UNAUTHORIZED);
        ObjectMapper mapper = new ObjectMapper();
        response.getWriter().write(mapper.writeValueAsString(ApiResponse.error(error)));
    }
}
