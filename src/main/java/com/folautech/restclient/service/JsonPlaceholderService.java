package com.folautech.restclient.service;

import com.folautech.restclient.dto.Post;
import com.folautech.restclient.dto.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class JsonPlaceholderService {

    private static final Logger log = LoggerFactory.getLogger(JsonPlaceholderService.class);
    private static final String BASE_URL = "https://jsonplaceholder.typicode.com";

    private final RestClient restClient;

    public JsonPlaceholderService(RestClient restClient) {
        this.restClient = restClient;
    }

    // ==================== GET Examples ====================

    /**
     * GET - Fetch a single user by ID
     */
    public User getUserById(Long id) {
        log.info("Fetching user with id: {}", id);

        return restClient.get()
                .uri(BASE_URL + "/users/{id}", id)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(User.class);
    }

    /**
     * GET - Fetch all posts
     */
    public List<Post> getAllPosts() {
        log.info("Fetching all posts");

        return restClient.get()
                .uri(BASE_URL + "/posts")
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<Post>>() {});
    }

    /**
     * GET - Fetch posts by user ID using query parameter
     */
    public List<Post> getPostsByUserId(Long userId) {
        log.info("Fetching posts for user id: {}", userId);

        return restClient.get()
                .uri(BASE_URL + "/posts?userId={userId}", userId)
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .body(new org.springframework.core.ParameterizedTypeReference<List<Post>>() {});
    }

    // ==================== POST Example ====================

    /**
     * POST - Create a new post
     */
    public Post createPost(Post post) {
        log.info("Creating new post: {}", post);

        return restClient.post()
                .uri(BASE_URL + "/posts")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(post)
                .retrieve()
                .body(Post.class);
    }

    // ==================== PUT Example ====================

    /**
     * PUT - Update an existing post
     */
    public Post updatePost(Long id, Post post) {
        log.info("Updating post with id: {}", id);

        return restClient.put()
                .uri(BASE_URL + "/posts/{id}", id)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(post)
                .retrieve()
                .body(Post.class);
    }
}
