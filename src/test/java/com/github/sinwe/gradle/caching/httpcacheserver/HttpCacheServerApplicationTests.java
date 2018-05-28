package com.github.sinwe.gradle.caching.httpcacheserver;

import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class HttpCacheServerApplicationTests {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    public void contextLoads() {
    }

    @Test
    public void testWithNoPriorCache() {
        String key = getKey();
        webTestClient.get().uri("/" + key).exchange().expectStatus().isNotFound();
    }

    @Test
    public void testStorageAndRetrieve() {
        String key = getKey();
        byte[] payload = getKey().getBytes();
        webTestClient.put().uri("/" + key).body(Flux.just(payload), byte[].class).exchange().expectStatus().isAccepted();
        webTestClient.get().uri("/" + key).exchange().expectStatus().isOk().expectBody(byte[].class)
                .isEqualTo(payload);
    }

    @NotNull
    private String getKey() {
        return UUID.randomUUID().toString().replace("-", "");
    }


}
