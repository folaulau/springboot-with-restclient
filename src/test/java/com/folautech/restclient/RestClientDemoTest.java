package com.folautech.restclient;

import com.folautech.restclient.dto.Post;
import com.folautech.restclient.dto.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Demonstration tests for Spring Boot RestClient.
 * These tests call real APIs (JSONPlaceholder) to showcase RestClient functionality.
 */
class RestClientDemoTest {

    private static final Logger log = LoggerFactory.getLogger(RestClientDemoTest.class);
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        // Create request factory with timeout settings
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5));
        requestFactory.setReadTimeout(Duration.ofSeconds(10));

        // Wrap with BufferingClientHttpRequestFactory to allow reading response body multiple times
        BufferingClientHttpRequestFactory bufferingFactory =
                new BufferingClientHttpRequestFactory(requestFactory);

        restClient = RestClient.builder()
                .requestFactory(bufferingFactory)
                .requestInterceptor(loggingInterceptor())
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

    // ==================== GET Request Examples ====================

    @Nested
    @DisplayName("GET Request Examples")
    class GetRequestTests {

        @Test
        @DisplayName("GET - Fetch single resource by ID")
        void testGetSingleResource() {
            log.info("\n\n========== TEST: GET Single Resource ==========\n");

            User user = restClient.get()
                    .uri(BASE_URL + "/users/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(User.class);

            assertNotNull(user);
            assertEquals(1L, user.getId());
            assertNotNull(user.getName());
            log.info("Retrieved user: {}", user);
        }

        @Test
        @DisplayName("GET - Fetch list of resources")
        void testGetListOfResources() {
            log.info("\n\n========== TEST: GET List of Resources ==========\n");

            List<Post> posts = restClient.get()
                    .uri(BASE_URL + "/posts")
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Post>>() {});

            assertNotNull(posts);
            assertFalse(posts.isEmpty());
            assertEquals(100, posts.size()); // JSONPlaceholder returns 100 posts
            log.info("Retrieved {} posts", posts.size());
        }

        @Test
        @DisplayName("GET - Fetch with query parameters")
        void testGetWithQueryParameters() {
            log.info("\n\n========== TEST: GET with Query Parameters ==========\n");

            // Fetch posts for a specific user using query parameter
            List<Post> userPosts = restClient.get()
                    .uri(BASE_URL + "/posts?userId={userId}", 1)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<Post>>() {});

            assertNotNull(userPosts);
            assertFalse(userPosts.isEmpty());
            // All posts should belong to user 1
            assertTrue(userPosts.stream().allMatch(post -> post.getUserId() == 1));
            log.info("Retrieved {} posts for user 1", userPosts.size());
        }

        @Test
        @DisplayName("GET - Fetch with ResponseEntity to access headers and status")
        void testGetWithResponseEntity() {
            log.info("\n\n========== TEST: GET with ResponseEntity ==========\n");

            ResponseEntity<User> response = restClient.get()
                    .uri(BASE_URL + "/users/{id}", 1)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(User.class);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertNotNull(response.getHeaders().getContentType());
            assertNotNull(response.getBody());
            log.info("Status: {}", response.getStatusCode());
            log.info("Content-Type: {}", response.getHeaders().getContentType());
            log.info("Body: {}", response.getBody());
        }
    }

    // ==================== POST Request Examples ====================

    @Nested
    @DisplayName("POST Request Examples")
    class PostRequestTests {

        @Test
        @DisplayName("POST - Create new resource")
        void testPostCreateResource() {
            log.info("\n\n========== TEST: POST Create Resource ==========\n");

            Post newPost = Post.builder()
                    .userId(1L)
                    .title("My New Post Title")
                    .body("This is the body of my new post created via RestClient")
                    .build();

            Post createdPost = restClient.post()
                    .uri(BASE_URL + "/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(newPost)
                    .retrieve()
                    .body(Post.class);

            assertNotNull(createdPost);
            assertNotNull(createdPost.getId()); // JSONPlaceholder assigns id=101
            assertEquals(newPost.getTitle(), createdPost.getTitle());
            assertEquals(newPost.getBody(), createdPost.getBody());
            log.info("Created post with id: {}", createdPost.getId());
        }

        @Test
        @DisplayName("POST - Create resource and check status code")
        void testPostWithResponseEntity() {
            log.info("\n\n========== TEST: POST with ResponseEntity ==========\n");

            Post newPost = Post.builder()
                    .userId(1L)
                    .title("Another Post")
                    .body("Body content here")
                    .build();

            ResponseEntity<Post> response = restClient.post()
                    .uri(BASE_URL + "/posts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(newPost)
                    .retrieve()
                    .toEntity(Post.class);

            assertEquals(HttpStatusCode.valueOf(201), response.getStatusCode()); // 201 Created
            assertNotNull(response.getBody());
            log.info("Response status: {}", response.getStatusCode());
        }
    }

    // ==================== PUT Request Examples ====================

    @Nested
    @DisplayName("PUT Request Examples")
    class PutRequestTests {

        @Test
        @DisplayName("PUT - Update existing resource")
        void testPutUpdateResource() {
            log.info("\n\n========== TEST: PUT Update Resource ==========\n");

            Post updatedPost = Post.builder()
                    .id(1L)
                    .userId(1L)
                    .title("Updated Post Title")
                    .body("This is the updated body content")
                    .build();

            Post result = restClient.put()
                    .uri(BASE_URL + "/posts/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(updatedPost)
                    .retrieve()
                    .body(Post.class);

            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("Updated Post Title", result.getTitle());
            log.info("Updated post: {}", result);
        }

        @Test
        @DisplayName("PUT - Update with ResponseEntity")
        void testPutWithResponseEntity() {
            log.info("\n\n========== TEST: PUT with ResponseEntity ==========\n");

            Post updatedPost = Post.builder()
                    .id(1L)
                    .userId(1L)
                    .title("Another Update")
                    .body("Updated body")
                    .build();

            ResponseEntity<Post> response = restClient.put()
                    .uri(BASE_URL + "/posts/{id}", 1)
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON)
                    .body(updatedPost)
                    .retrieve()
                    .toEntity(Post.class);

            assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
            assertNotNull(response.getBody());
            log.info("Response status: {}", response.getStatusCode());
        }
    }

    // ==================== Timeout Configuration Examples ====================

    @Nested
    @DisplayName("Timeout Configuration Examples")
    class TimeoutTests {

        @Test
        @DisplayName("RestClient with custom timeouts")
        void testCustomTimeouts() {
            log.info("\n\n========== TEST: Custom Timeout Configuration ==========\n");

            // Create a RestClient with specific timeout settings
            SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();

            // Connection timeout: max time to establish connection
            requestFactory.setConnectTimeout(Duration.ofSeconds(3));

            // Read timeout: max time to wait for response data
            requestFactory.setReadTimeout(Duration.ofSeconds(5));

            RestClient clientWithTimeouts = RestClient.builder()
                    .requestFactory(requestFactory)
                    .build();

            // This should complete within the timeout
            User user = clientWithTimeouts.get()
                    .uri(BASE_URL + "/users/1")
                    .retrieve()
                    .body(User.class);

            assertNotNull(user);
            log.info("Request completed within timeout limits");
            log.info("Connection timeout: 3 seconds");
            log.info("Read timeout: 5 seconds");
        }
    }
}
