package com.github.tomcooperca.bottle;

import com.github.tomcooperca.bottle.repository.Message;
import com.github.tomcooperca.bottle.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;

@SpringBootApplication
public class BottleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BottleApplication.class, args);
	}

	@Profile("local")
	@Component
	@RequiredArgsConstructor
	public static class SaveTestMessages implements CommandLineRunner {

		private final MessageRepository messageRepository;

		@Override
		public void run(String... args) throws Exception {
			Files.lines(Paths.get(ClassLoader.getSystemResource("test_messages.txt").toURI()))
					.forEach(s -> messageRepository.save(new Message(new Random().nextLong(), s, "localhost")));
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

		@PostMapping(consumes = MediaType.TEXT_PLAIN_VALUE)
        public ResponseEntity addMessage(HttpServletRequest request, @RequestBody String message) {
		    messageService.saveMessage(message, request.getRemoteAddr());
		    return ResponseEntity.accepted().build();
        }

	}
}
