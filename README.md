# Gradle HTTP Build Cache Server

[![Build Status](https://github.com/sinwe/http-gradle-cache-server/actions/workflows/gradle.yml/badge.svg)](https://github.com/sinwe/http-gradle-cache-server/actions/runs/4398313516)
[![CodeQL](https://github.com/sinwe/http-gradle-cache-server/actions/workflows/codeql-analysis.yml/badge.svg)](https://github.com/sinwe/http-gradle-cache-server/actions/workflows/codeql-analysis.yml)

This is an alternative Gradle build cache server.
There are 2 mode that this cache server can be used:
#### Persisted Mode
Store cache into disk. At this moment the maximum cache size is 64GB, enough for most build.

What it does:
* Cache gradle build
* Persist into disk so it survives server being restarted

What it does not do:
* No eviction strategy

#### In Memory Mode (Default)
Store cache into memory. The total cache entry stored in the cache is limited by the heap allocated to JVM

What it does:
* Cache gradle build
* Store in memory

Option for this mode:
* Replicate to another node if configured
* Evict entries to free up memory using LRU

## How to run
    $ java -jar http-cache-server-0.0.1.jar
    
## Usage
Point your Gradle build at a running instance of this server by adding the following to your `settings.gradle`:

    buildCache {
        remote(HttpBuildCache) {
            url = "https://your-cache-server.example.com:32500/"
            enabled = true
            push = true
        }
    }
