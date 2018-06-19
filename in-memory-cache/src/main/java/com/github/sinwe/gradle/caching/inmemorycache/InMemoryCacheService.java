package com.github.sinwe.gradle.caching.inmemorycache;

import io.vertx.core.Handler;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.nio.charset.Charset;
import java.util.Base64;
import java.util.concurrent.ConcurrentMap;

@Service
public class InMemoryCacheService {
    private static final Logger LOGGER = LoggerFactory.getLogger(InMemoryCacheService.class);
    private final EventBus eventBus;
    private final ConcurrentMap<String, String> storage;

    @Autowired
    public InMemoryCacheService(EventBus eventBus, ConcurrentMap<String, String> storage) {
        this.eventBus = eventBus;
        this.storage = storage;
    }

    @PostConstruct
    public void start() {
        this.eventBus.consumer("public-store", (Handler<Message<JsonObject>>) event -> {
            JsonObject jsonObject = event.body();
            String key = jsonObject.getString("key");
            String encoded = jsonObject.getString("cache");
            LOGGER.debug("Store: "+ encoded);
            storage.put(key, encoded);
            event.reply(new JsonObject().put("response", "OK"));
        });
        this.eventBus.consumer("public-load", (Handler<Message<JsonObject>>) event -> {
            JsonObject jsonObject = event.body();
            String key = jsonObject.getString("key");
            if(storage.containsKey(key)) {
                String cache = storage.get(key);
                System.out.println("Load: "+ cache);
                event.reply(new JsonObject().put("key", key).put("cache", cache));
            } else {
                event.fail(404, "Cache not found");
            }
        });
    }
}
