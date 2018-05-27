package com.github.sinwe.gradle.caching.httpcacheserver;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@RunWith(SpringRunner.class)
@SpringBootTest
public class HttpCacheServerApplicationTests {

    @Test
    public void contextLoads() {
    }

    @TestConfiguration
    public static class MapConfig {
        @Bean
        public ConcurrentMap<String, byte[]> map() {
            return new ConcurrentHashMap<>();
        }
    }
}
