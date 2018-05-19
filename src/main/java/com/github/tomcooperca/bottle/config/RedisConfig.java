package com.github.tomcooperca.bottle.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.stereotype.Component;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;

@Configuration
@EnableRedisRepositories
public class RedisConfig {

    @Bean
    RedisConnectionFactory connectionFactory() {
        return new LettuceConnectionFactory();
    }

    @Bean
    RedisTemplate<?, ?> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<byte[], byte[]> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        return template;
    }

    @Component
    @ConditionalOnProperty(prefix = "bottle.embed", name = "enabled", havingValue = "true", matchIfMissing = true)
    public class EmbeddedRedisServer {

        private final RedisServer redisServer;

        public EmbeddedRedisServer() throws IOException {
            this.redisServer = new RedisServer();
        }

        @PostConstruct
        public void start() throws IOException {
            redisServer.start();
        }

        @PreDestroy
        public void stop() {
            redisServer.stop();
        }
    }

}

