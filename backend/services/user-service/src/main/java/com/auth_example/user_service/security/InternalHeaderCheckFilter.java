package com.auth_example.user_service.security;

import com.auth_example.common_service.core.responses.ApiError;
import com.auth_example.common_service.core.responses.ApiErrorCode;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Slf4j
public class InternalHeaderCheckFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader("X-Internal-Use");
        if (header == null || !header.equals("gateway")) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);

            ApiError apiError = new ApiError(ApiErrorCode.UNAUTHORIZED, "you do not have the access to this api");
            ApiResponse<Object> apiResponse = ApiResponse.error(apiError);

            ObjectMapper om = new ObjectMapper();
            String jsonResponse = om.writeValueAsString(apiResponse);

            response.getWriter().write(jsonResponse);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
