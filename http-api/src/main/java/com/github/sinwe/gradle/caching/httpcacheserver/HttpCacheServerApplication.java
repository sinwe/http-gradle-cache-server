package com.github.sinwe.gradle.caching.httpcacheserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class HttpCacheServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(HttpCacheServerApplication.class, args);
    }
}
