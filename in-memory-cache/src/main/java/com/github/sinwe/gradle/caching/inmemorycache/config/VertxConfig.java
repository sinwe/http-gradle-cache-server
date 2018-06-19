package com.github.sinwe.gradle.caching.inmemorycache.config;

import com.hazelcast.core.HazelcastInstance;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.spi.cluster.ClusterManager;
import io.vertx.spi.cluster.hazelcast.HazelcastClusterManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import static java.util.concurrent.TimeUnit.SECONDS;

@Configuration
public class VertxConfig {

    @Autowired
    public VertxConfig(AbstractApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Bean
    @DependsOn("hazelcastInstance")
    public ClusterManager clusterManager(@Autowired @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        return new HazelcastClusterManager(hazelcastInstance);
    }

    @Bean
    public VertxOptions vertxOptions(@Autowired ClusterManager clusterManager) {
        return new VertxOptions()
                .setClustered(true)
                .setClusterManager(clusterManager);
    }

    @Bean(destroyMethod = "close")
    public Vertx vertx(@Autowired VertxOptions vertxOptions) throws InterruptedException {
        BlockingQueue<Vertx> queue = new ArrayBlockingQueue<>(1);
        Vertx.clusteredVertx(vertxOptions, asyncResult -> {
            try {
                if(asyncResult.succeeded()) {
                    queue.put(asyncResult.result());
                } else {
                    throw  asyncResult.cause();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        Vertx vertx = queue.poll(10, SECONDS);
        if(vertx == null) {
            applicationContext.stop();
        }
        return vertx;
    }

    private final AbstractApplicationContext applicationContext;

    @Bean
    public EventBus eventBus(@Autowired Vertx vertx) {
        return vertx.eventBus();
    }
}
