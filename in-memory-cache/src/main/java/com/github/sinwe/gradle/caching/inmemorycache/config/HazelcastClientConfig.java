package com.github.sinwe.gradle.caching.inmemorycache.config;

import com.hazelcast.config.*;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.hazelcast.config.EvictionPolicy.NONE;

@Configuration
public class HazelcastClientConfig {

    @Value("${hazelcast.port:5701}")
    private int port;

    @Bean
    public Map<String, MultiMapConfig> multiMapConfigMap() {
        Map<String, MultiMapConfig> map = new HashMap<>();
        map.put("__vertx.subs", new MultiMapConfig("__vertx.subs").setBackupCount(1));
        return map;
    }

    @Bean
    public Map<String, MapConfig> mapConfigMap() {
        Map<String, MapConfig> map = new HashMap<>();
        map.put("__vertx.haInfo", new MapConfig("__vertx.haInfo")
        .setTimeToLiveSeconds(0)
        .setMaxIdleSeconds(0)
        .setEvictionPolicy(NONE)
        .setMaxSizeConfig(new MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.PER_NODE))
        .setMergePolicy("com.hazelcast.map.merge.LatestUpdateMapMergePolicy"));
        return map;
    }

    @Bean
    public Map<String, SemaphoreConfig> semaphoreConfigMap() {
        Map<String, SemaphoreConfig> map = new HashMap<>();
        map.put("__vertx.*", new SemaphoreConfig().setInitialPermits(1));
        return map;
    }

    @Bean
    public Config config(@Autowired Map<String, MultiMapConfig> multiMapConfig,
                         @Autowired Map<String, MapConfig> mapConfig,
                         @Autowired Map<String, SemaphoreConfig> semaphoreConfig) {
        return new Config()
                .setProperty("hazelcast.shutdownhook.enabled", "false")
                .setMultiMapConfigs(multiMapConfig)
                .setMapConfigs(mapConfig)
                .setSemaphoreConfigs(semaphoreConfig)
                .setNetworkConfig(new NetworkConfig().setPort(port));
    }

    @Bean
    public HazelcastInstance hazelcastInstance(@Autowired Config config) {
        return Hazelcast.newHazelcastInstance(config);
    }

    @Bean
    public IMap<String, String> concurrentMap(@Autowired @Qualifier("hazelcastInstance") HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("gradle-build-cache");
    }
}
