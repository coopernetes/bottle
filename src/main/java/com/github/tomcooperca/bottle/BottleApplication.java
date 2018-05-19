package com.github.tomcooperca.bottle;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@SpringBootApplication
public class BottleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BottleApplication.class, args);
	}

	@RestController
	@RequiredArgsConstructor
	public class DefaultController {

		private final DisplayService displayService;

		@GetMapping("/")
		public ResponseEntity index() {
			// average reading speed = 200 wpm = 3(ish) words per second
		    return ResponseEntity.ok(displayService.randomMessage());
		}

		@PostMapping(value = "/", consumes = MediaType.TEXT_PLAIN_VALUE)
        public ResponseEntity addMessage(HttpServletRequest request, @RequestBody String message) {
		    displayService.saveMessage(message, request.getRemoteAddr());
		    return ResponseEntity.accepted().build();
        }

	}
}
