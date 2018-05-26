package com.github.sinwe.gradle.caching.httpcacheserver.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentMap;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@EnableAutoConfiguration
public class CacheController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CacheController.class);
    private final ConcurrentMap<String, byte[]> storage;

    public CacheController(@Autowired ConcurrentMap<String, byte[]> storage) {
        this.storage = storage;
    }

    @GetMapping("/")
    String home() {
        return "Welcome to HTTP Cache Server - An alternative HTTP based Gradle cache server!!!";
    }

    @PutMapping("/{key}")
    @ResponseStatus(ACCEPTED)
    void store(@PathVariable String key, @RequestBody byte[] payload) {
        LOGGER.info("Storing {} -> {}", key, payload);
        storage.put(key, payload);
    }

    @GetMapping("/{key}")
    ResponseEntity<byte[]> load(@PathVariable String key) {
        if(storage.containsKey(key)) {
            LOGGER.info("Returning payload for key {}", key);
            return new ResponseEntity<>(storage.get(key), OK);
        } else {
            LOGGER.info("No cache available for key {}", key);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
