package com.auth_example.common_service.core.rest;

import com.auth_example.common_service.core.exceptions.RemoteServiceException;
import com.auth_example.common_service.core.responses.ApiResponse;
import com.auth_example.common_service.exceptions.ClientErrorException;
import com.auth_example.common_service.exceptions.ExternalApiException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.rmi.UnexpectedException;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

public class BaseRestClient {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;



    public BaseRestClient(RestClient.Builder restClientBuilder, ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.restClient = restClientBuilder
                .defaultStatusHandler(
                        HttpStatusCode::isError,
                        (request, response) -> {
                            InputStream bodyStream = response.getBody();
                            String body = new BufferedReader(new InputStreamReader(bodyStream, StandardCharsets.UTF_8))
                                    .lines()
                                    .collect(Collectors.joining("\n"));

                            HttpStatusCode status = response.getStatusCode();
//                            if (status.is4xxClientError()) {
//                                throw new ClientErrorException("4xx Error from " + request.getURI() + ": " + body);
//                            } else if (status.is5xxServerError()) {
//                                throw new ServerErrorException("5xx Error from " + request.getURI() + ": " + body);
//                            } else {
//                                throw new UnexpectedException("Unexpected HTTP error from " + request.getURI() + ": " + body);
//                            }
                            try {
                                ApiResponse<?> apiResponse = objectMapper.readValue(body, ApiResponse.class);
                                if (apiResponse != null && apiResponse.getError() != null) {
                                    throw new RemoteServiceException(apiResponse.getError());
                                }
                            } catch (Exception e) {
                                throw new UnexpectedException("Failed to parse error response from " + request.getURI() + ": " + body, e);
                            }
                        }
                )
                .build();
    }

    public <T> ApiResponse<T> get(String uri, Class<T> responseType) {
        ParameterizedTypeReference<ApiResponse<T>> typeRef = ParameterizedTypeReference.forType(
                ResolvableType.forClassWithGenerics(ApiResponse.class, responseType). getType()
        );

        try {
            return restClient.get()
                    .uri(uri)
                    .retrieve()
                    .body(typeRef);
        } catch (RestClientException e) {
            throw new ExternalApiException("Error during GET to " + uri, e);
        }
    }

    public <T> ApiResponse<T> post(String uri, Object body, Class<T> responseType) {
        ParameterizedTypeReference<ApiResponse<T>> typeRef = ParameterizedTypeReference.forType(
                ResolvableType.forClassWithGenerics(ApiResponse.class, responseType). getType()
        );

        try {
            return restClient.post()
                    .uri(uri)
                    .contentType(APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(typeRef);
        } catch (RestClientException e) {
            throw new ExternalApiException("Error during POST to " + uri, e);
        }
    }
}
