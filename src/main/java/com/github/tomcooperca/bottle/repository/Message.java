package com.github.tomcooperca.bottle.repository;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.util.Locale;
import java.util.UUID;

@RedisHash("message")
@Value
public class Message {
    @Id
    private UUID id;
    @Indexed
    private String content;
    private String originator;
    private Locale locale;
}
