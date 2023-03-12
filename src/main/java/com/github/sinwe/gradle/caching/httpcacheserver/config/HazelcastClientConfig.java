package com.github.sinwe.gradle.caching.httpcacheserver.config;

import com.hazelcast.client.config.ClientConfig;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.map.IMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("InMemory")
public class HazelcastClientConfig {

    @Value("${hazelcast.port:5701}")
    private int port;

    @Bean
    public ClientConfig clientConfig() {
        ClientConfig config = new ClientConfig();
        config.getNetworkConfig().addAddress("localhost:"+port);
        return config;
    }

    @Bean
    public IMap<String, byte[]> concurrentMap(@Autowired HazelcastInstance hazelcastInstance) {
        return hazelcastInstance.getMap("gradle-build-cache");
    }
}
