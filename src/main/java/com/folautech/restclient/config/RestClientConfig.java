package com.folautech.restclient.config;

import com.folautech.restclient.utility.RestClientLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.stream.Collectors;

@Configuration
public class RestClientConfig {

    private static final Logger log = LoggerFactory.getLogger(RestClientConfig.class);

    @Bean
    public RestClient restClient() {
        // Create request factory with timeout settings
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));  // Connection timeout
        requestFactory.setReadTimeout(Duration.ofSeconds(10));    // Read timeout

        // Wrap with BufferingClientHttpRequestFactory to allow reading response body multiple times
        BufferingClientHttpRequestFactory bufferingFactory =
            new BufferingClientHttpRequestFactory(requestFactory);

        return RestClient.builder()
                .requestFactory(bufferingFactory)
                .requestInterceptor(new RestClientLog(true))
                .build();
    }

    private ClientHttpRequestInterceptor loggingInterceptor() {
        return (request, body, execution) -> {
            // Log request
            log.info("============================= REQUEST =============================");
            log.info("URI         : {}", request.getURI());
            log.info("Method      : {}", request.getMethod());
            log.info("Headers     : {}", request.getHeaders());
            if (body.length > 0) {
                log.info("Body        : {}", new String(body, StandardCharsets.UTF_8));
            }

            // Execute the request
            ClientHttpResponse response = execution.execute(request, body);

            // Log response
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

            return response;
        };
    }
}
