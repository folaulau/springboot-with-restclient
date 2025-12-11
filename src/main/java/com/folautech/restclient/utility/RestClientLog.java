package com.folautech.restclient.utility;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Slf4j
public class RestClientLog implements ClientHttpRequestInterceptor {

    private boolean enabled = true;

    public RestClientLog() {
        this(true);
    }

    public RestClientLog(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        if (enabled) {
            logRequest(request, body);
        }

        ClientHttpResponse response = execution.execute(request, body);

        if (enabled) {
            logResponse(response);
        }
        return response;
    }

    private void logRequest(HttpRequest request, byte[] body) {
        log.info("============================= REQUEST =============================");
        log.info("URI         : {}", request.getURI());
        log.info("Method      : {}", request.getMethod());
        log.info("Headers     : {}", request.getHeaders());
        if (body.length > 0) {
            log.info("Body        : {}", new String(body, StandardCharsets.UTF_8));
        } else {
            log.info("Body        : {}");
        }
    }

    private void logResponse(ClientHttpResponse response) {
        try {
            log.info("============================= RESPONSE ============================");
            log.info("Status Code : {}", response.getStatusCode());
            log.info("Status Text : {}", response.getStatusText());
            log.info("Headers     : {}", response.getHeaders());

            // Read and log response body
            String responseBody = new BufferedReader(
                    new InputStreamReader(response.getBody(), StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
            log.info("Body        : {}", responseBody);
            log.info("===================================================================");
        } catch (Exception e) {
            log.warn("Exception msg: {}", e.getMessage());
        }
    }
}
