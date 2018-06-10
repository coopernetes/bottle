package com.github.tomcooperca.bottle.message;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("message")
@Value
public class Message {
    @Id
    private String uuid;
    @Indexed
    private String content;
    private String originator;
    private String locale;
}
