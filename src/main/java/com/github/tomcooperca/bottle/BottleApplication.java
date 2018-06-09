package com.github.tomcooperca.bottle;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
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

		private final MessageService messageService;

		@Override
		public void run(String... args) throws Exception {
			URI testMessages = ClassLoader.getSystemResource("test_messages.txt").toURI();

			Path path = Paths.get(testMessages);

			Files.lines(path).forEach(s -> messageService.saveMessage(s, "localhost"));
		}
	}

	@Profile("test")
	@Component
	@RequiredArgsConstructor
	public static class SaveTestMessagesFromUrl implements CommandLineRunner {

		@Value("${bottle.test.url}")
		String url;

		private final MessageService messageService;

		@Override
		public void run(String... args) throws Exception {
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> responseEntity = restTemplate.getForEntity(URI.create(url), String.class);
			if (responseEntity.getStatusCode().is2xxSuccessful()) {
				Arrays.asList(responseEntity.getBody().split("\\n"))
						.forEach(s -> messageService.saveMessage(s, "localhost"));
			}
		}
	}

	@RestController
	@RequiredArgsConstructor
    @RequestMapping("/message")
	public class DefaultController {

		private final MessageService messageService;

		@GetMapping()
		public ResponseEntity index() {
			// average reading speed = 200 wpm = 3(ish) words per second
		    return ResponseEntity.ok(messageService.randomMessage());
		}

		@GetMapping("/all")
        public ResponseEntity all() {
		    return ResponseEntity.ok(messageService.allMessages());
        }

		@PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
        public ResponseEntity addMessage(HttpServletRequest request, @RequestBody String message) {
		    messageService.saveMessage(message, request.getRemoteAddr());
		    return ResponseEntity.accepted().build();
        }

	}
}
