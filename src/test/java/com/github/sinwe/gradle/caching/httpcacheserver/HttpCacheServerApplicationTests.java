package com.github.sinwe.gradle.caching.httpcacheserver;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.util.UUID;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
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
