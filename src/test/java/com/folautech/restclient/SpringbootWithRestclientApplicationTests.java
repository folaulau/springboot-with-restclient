package com.folautech.restclient;

import com.folautech.restclient.dto.Post;
import com.folautech.restclient.dto.User;
import com.folautech.restclient.service.JsonPlaceholderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class SpringbootWithRestclientApplicationTests {

	@Autowired
	JsonPlaceholderService jsonPlaceholderService;

	@Test
	void test_get() {

		User user = jsonPlaceholderService.getUserById(1L);

		log.info("user: {}", user);
	}

	@Test
	void test_post() {

			System.out.println("\n--- POST Example: Create new post ---");
			Post newPost = Post.builder()
					.userId(1L)
					.title("My New Post Title")
					.body("This is the body of my new post created via RestClient")
					.build();
			Post createdPost = jsonPlaceholderService.createPost(newPost);
			log.info("Created post with id: {}", createdPost.getId());
	}

	@Test
	void test_put() {
		System.out.println("\n--- PUT Example: Update existing post ---");
		Post updatePost = Post.builder()
				.id(1L)
				.userId(1L)
				.title("Updated Post Title")
				.body("This is the updated body of the post")
				.build();
		Post updatedPost = jsonPlaceholderService.updatePost(1L, updatePost);
		log.info("Updated post: {}", updatedPost);
	}

}
