package com.auth_example.common_service.core.rest;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;

import java.util.function.Consumer;

@RequiredArgsConstructor
public class RequestOption {

    private final Consumer<HttpHeaders> headersCustomizer;

    public static RequestOption withHeaders(Consumer<HttpHeaders> headersCustomizer) {
        return new RequestOption(headersCustomizer);
    }

    public static RequestOption withInternalHeaders(Consumer<HttpHeaders> headersCustomizer) {
        return new RequestOption(headers -> {
            headers.add("X-Internal-Use", "gateway");
            headersCustomizer.accept(headers);
        });
    }

    public static RequestOption internalNone() {
        return new RequestOption(headers -> {
            headers.add("X-Internal-Use", "gateway");
        });
    }

    public static RequestOption none() {
        return new RequestOption(headers -> {
        });
    }

    public Consumer<HttpHeaders> headers() {
        return headersCustomizer;
    }
}
