package com.github.tomcooperca.bottle;

import com.github.tomcooperca.bottle.config.AppProperties;
import com.github.tomcooperca.bottle.message.Message;
import com.github.tomcooperca.bottle.message.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Locale;
import java.util.UUID;

@SpringBootApplication
public class BottleApplication {

	public static void main(String[] args) {
		SpringApplication.run(BottleApplication.class, args);
	}

	@Profile("test")
	@Component
	@RequiredArgsConstructor
	@EnableConfigurationProperties(AppProperties.class)
	public static class SaveTestMessages implements CommandLineRunner {

		private final MessageRepository messageRepository;
		private final AppProperties appProperties;

		@Override
		public void run(String... args) throws Exception {
			appProperties.getMessages().forEach(s -> messageRepository.save(new Message(UUID.randomUUID().toString(), s,
							"localhost", Locale.getDefault().toString())));
		}
	}
}
