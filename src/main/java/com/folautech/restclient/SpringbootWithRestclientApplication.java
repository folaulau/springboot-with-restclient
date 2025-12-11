package com.folautech.restclient;

import com.folautech.restclient.dto.Post;
import com.folautech.restclient.dto.User;
import com.folautech.restclient.service.JsonPlaceholderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

@SpringBootApplication
public class SpringbootWithRestclientApplication {

	private static final Logger log = LoggerFactory.getLogger(SpringbootWithRestclientApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringbootWithRestclientApplication.class, args);
	}

	@Order(1)
	@Bean
	public CommandLineRunner environmentInfoRunner(ApplicationContext ctx) {
		return args -> {

			// Display Environmental Useful Variables
			try {
				System.out.println("\n");
				Runtime runtime = Runtime.getRuntime();
				double mb = 1048576;// megabtye to byte
				double gb = 1073741824;// gigabyte to byte
				Environment env = ctx.getEnvironment();
				TimeZone timeZone = TimeZone.getDefault();

				System.out.println("************************ Springboot with Restclient ***********************************");
				System.out.println("** Active Profile: " + Arrays.toString(env.getActiveProfiles()));
				System.out.println("** Port: " + env.getProperty("server.port"));
				System.out.println("** Timezone: " + timeZone.getID());
				System.out.println("** TimeStamp: " + new Date().toInstant().toString());

				String contextPath = env.getProperty("server.servlet.context-path");

				if(contextPath == null) {
					contextPath = "";
				}

				System.out.println("************************* Java - JVM *********************************");
				System.out.println("** Number of processors: " + runtime.availableProcessors());
				String processName = ManagementFactory.getRuntimeMXBean().getName();
				System.out.println("** Process ID: " + processName.split("@")[0]);
				System.out.println("** Total memory: " + (runtime.totalMemory() / mb) + " MB = " + (runtime.totalMemory() / gb) + " GB");
				System.out.println("** Max memory: " + (runtime.maxMemory() / mb) + " MB = " + (runtime.maxMemory() / gb) + " GB");
				System.out.println("** Free memory: " + (runtime.freeMemory() / mb) + " MB = " + (runtime.freeMemory() / gb) + " GB");
				System.out.println();
				System.out.println("**********************************************************************");

			} catch (Exception e) {
				e.printStackTrace();
				System.err.println("Exception, commandlineRunner -> " + e.getMessage());
			}
			System.out.println("\n");
		};
	}

	@Order(2)
	@Bean
	public CommandLineRunner restClientDemoRunner(JsonPlaceholderService jsonPlaceholderService) {
		return args -> {
			System.out.println("\n");
			System.out.println("==================== RestClient Demo ====================");

//			// ==================== GET Examples ====================
//			System.out.println("\n--- GET Example: Fetch single user ---");
//			User user = jsonPlaceholderService.getUserById(1L);
//			log.info("Retrieved user: {}", user);
//
//			System.out.println("\n--- GET Example: Fetch posts by user ID ---");
//			List<Post> userPosts = jsonPlaceholderService.getPostsByUserId(1L);
//			log.info("Retrieved {} posts for user 1", userPosts.size());
//
//			// ==================== POST Example ====================
//			System.out.println("\n--- POST Example: Create new post ---");
//			Post newPost = Post.builder()
//					.userId(1L)
//					.title("My New Post Title")
//					.body("This is the body of my new post created via RestClient")
//					.build();
//			Post createdPost = jsonPlaceholderService.createPost(newPost);
//			log.info("Created post with id: {}", createdPost.getId());
//
//			// ==================== PUT Example ====================
//			System.out.println("\n--- PUT Example: Update existing post ---");
//			Post updatePost = Post.builder()
//					.id(1L)
//					.userId(1L)
//					.title("Updated Post Title")
//					.body("This is the updated body of the post")
//					.build();
//			Post updatedPost = jsonPlaceholderService.updatePost(1L, updatePost);
//			log.info("Updated post: {}", updatedPost);

			System.out.println("\n==================== Demo Complete ====================\n");
		};
	}
}
