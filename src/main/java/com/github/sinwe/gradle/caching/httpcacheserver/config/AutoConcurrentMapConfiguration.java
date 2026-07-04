package com.github.sinwe.gradle.caching.httpcacheserver.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Configuration
public class AutoConcurrentMapConfiguration {

    // @ConditionalOnMissingBean is evaluated in classpath-scan order, and this class sorts
    // alphabetically before ChronicleMapConfig/HazelcastClientConfig, so it can't see their
    // profile-specific beans in time and always creates this fallback anyway. The profile-specific
    // beans are marked @Primary so autowiring picks them even when both exist.
    @Bean
    @ConditionalOnMissingBean
    public ConcurrentMap<String, byte[]> concurrentMap() {
        return new ConcurrentHashMap<>();
    }
}
