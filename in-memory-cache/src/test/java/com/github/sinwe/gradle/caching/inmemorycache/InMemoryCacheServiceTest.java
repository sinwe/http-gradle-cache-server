package com.github.sinwe.gradle.caching.inmemorycache;

import com.github.sinwe.gradle.caching.inmemorycache.config.HazelcastClientConfig;
import com.github.sinwe.gradle.caching.inmemorycache.config.VertxConfig;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.eventbus.DeliveryOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.eventbus.Message;
import io.vertx.core.eventbus.ReplyException;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestOptions;
import io.vertx.ext.unit.TestSuite;
import io.vertx.ext.unit.report.ReportOptions;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Base64;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@ContextConfiguration(classes = {HazelcastClientConfig.class,
        VertxConfig.class,
        InMemoryCacheService.class})
public class InMemoryCacheServiceTest extends AbstractJUnit4SpringContextTests {

    @Autowired
    private EventBus eventBus;

    @Autowired
    private Vertx vertx;

    private byte[] bytes = "This is my object".getBytes();
    private String key = UUID.randomUUID().toString();

    @Test
    public void testStoreObjectAndLoad() throws InterruptedException {
        String encoded = Base64.getEncoder().encodeToString(bytes);
        System.out.println("Publish encoded: "+encoded);
        CountDownLatch latch = new CountDownLatch(1);
        eventBus.send("public-store", new JsonObject().put("key", key).put("cache", encoded),
                (Handler<AsyncResult<Message<JsonObject>>>) event -> {
                    if (event.succeeded()) {
                        JsonObject jsonObject = event.result().body();
                        assertEquals("OK", jsonObject.getString("response"));
                        latch.countDown();
                    } else {
                        fail(event.cause().getMessage());
                    }
                });
        assertTrue(latch.await(5, TimeUnit.SECONDS));

        CountDownLatch latch2 = new CountDownLatch(1);
        eventBus.send("public-load", new JsonObject().put("key", key), (Handler<AsyncResult<Message<JsonObject>>>) event -> {
            if(event.succeeded()) {
                JsonObject jsonObject = event.result().body();
                String receivedEncoded = jsonObject.getString("cache");
                System.out.println("Received encoded: "+ receivedEncoded);
                byte[] receivedDecoded = Base64.getDecoder().decode(receivedEncoded);
                String value = new String(receivedDecoded);
                System.out.println("Value: "+ value);
                System.out.println("bytes: "+ Arrays.toString(bytes) + " " + new String(bytes));
                System.out.println("receivedDecoded: " + Arrays.toString(receivedDecoded) + " " + new String(receivedDecoded));
                assertEquals(Arrays.toString(bytes), Arrays.toString(receivedDecoded));
                latch2.countDown();
            } else {
                fail(event.cause().getMessage());
            }
        });
        assertTrue(latch2.await(5, TimeUnit.SECONDS));
    }

    @Test
    public void testLoadObjectThatDoesNotExist() throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        eventBus.send("public-load", new JsonObject().put("key", UUID.randomUUID().toString()), (Handler<AsyncResult<Message<byte[]>>>) event -> {
            assertTrue(event.failed());
            ReplyException replyFailure = (ReplyException) event.cause();
            assertEquals(404, replyFailure.failureCode());
            assertEquals("Cache not found", replyFailure.getMessage());
            latch.countDown();
        });
        assertTrue(latch.await(5, TimeUnit.SECONDS));
    }
}