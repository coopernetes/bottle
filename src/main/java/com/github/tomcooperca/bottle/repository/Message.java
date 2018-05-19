package com.github.tomcooperca.bottle.repository;

import lombok.Value;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash("bottle")
@Value
public class Message {
    @Id
    private long id;
    @Indexed
    private String content;
    private String originator;

}