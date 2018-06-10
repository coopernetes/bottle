package com.github.tomcooperca.bottle;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

@SpringBootApplication
public class BottleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BottleApplication.class, args);
	}

	@Profile("local")
	@Component
	@RequiredArgsConstructor
	public static class SaveTestMessages implements CommandLineRunner {

		@Value("${server.port}")
		int port;

		private RestTemplate restTemplate = new RestTemplate();

		@Override
		public void run(String... args) throws Exception {
			URI testMessages = ClassLoader.getSystemResource("test_messages.txt").toURI();
			Files.lines(Paths.get(testMessages))
					.forEach(s -> restTemplate.postForLocation("http://localhost:".concat(String.valueOf(port)).concat("/message"), s));
		}
	}

	@Profile("test")
	@Component
    @ConditionalOnProperty(prefix = "bottle.test", name = "url")
	@RequiredArgsConstructor
	public static class SaveTestMessagesFromUrl implements CommandLineRunner {

		@Value("${bottle.test.url}")
		String url;

		@Value("${server.port}")
		int port;

		private RestTemplate restTemplate = new RestTemplate();

		@Override
		public void run(String... args) throws Exception {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(URI.create(url), String.class);
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				Arrays.asList(responseEntity.getBody().split("\\n"))
						.forEach(s -> restTemplate.postForLocation("http://localhost:".concat(String.valueOf(port)).concat("/message"), s));
			}
		}
	}
}
