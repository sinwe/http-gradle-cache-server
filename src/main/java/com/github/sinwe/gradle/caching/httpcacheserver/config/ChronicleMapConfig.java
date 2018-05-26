package com.github.sinwe.gradle.caching.httpcacheserver.config;

import net.openhft.chronicle.map.ChronicleMap;
import net.openhft.chronicle.map.ChronicleMapBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class ChronicleMapConfig {
    @Bean(destroyMethod = "close")
    @Order(Integer.MIN_VALUE)
    public ChronicleMap<String, byte[]> chronicleMap(@Autowired File cacheFile) throws IOException {
        return ChronicleMapBuilder.of(String.class, byte[].class)
                .name("gradle-build-cache")
                .constantKeySizeBySample("be55b029576c107a597a765e79be14a7")
                .averageValueSize(500000)
                .entries(100000)
                .replication()
                .createOrRecoverPersistedTo(cacheFile);
    }

    @Bean
    public File cacheDirectory() throws FileNotFoundException {
        File cacheDirectory = new File("./data");
        if(cacheDirectory.exists() && cacheDirectory.isDirectory()) return cacheDirectory;
        else if (cacheDirectory.mkdirs()) return cacheDirectory;
        else throw new FileNotFoundException("Unable to create cache directory " + cacheDirectory.toString());
    }

    @Bean
    public File cacheFile(@Autowired File cacheDirectory) {
        return new File(cacheDirectory, "gradle-cache-data");
    }
}
