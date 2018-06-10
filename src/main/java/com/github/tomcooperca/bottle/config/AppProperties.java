package com.github.tomcooperca.bottle.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@Setter
@Getter
@ConfigurationProperties(prefix = "bottle.test")
public class AppProperties {
    List<String> messages;
    boolean embedded;
}
