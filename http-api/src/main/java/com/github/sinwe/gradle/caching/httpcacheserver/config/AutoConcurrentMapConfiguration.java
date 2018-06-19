package com.github.sinwe.gradle.caching.httpcacheserver.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class AutoConcurrentMapConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ConcurrentMap<String, byte[]> concurrentMap() {
        return new ConcurrentHashMap<>();
    }
}
