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
    
## Open source usage
For building open source project using gradle, a free cache server is available to use.
Add the following in your `settings.gradle`:
    
    buildCache {
        remote(HttpBuildCache) {
            url = "https://cache1.winar.to:32500/"
            enabled = true
            push = true
        }
    } 

Note: the cache server above is using in-memory mode with replication.
It is also configured to evict entries using LRU to avoid running out of memory.
It is however having a quite big heap allocated to it.
It also provide no guarantee of the cache to be available all the time.
Should you need a persisted mode and uptime guarantee dedicated to your project, please contact for pricing.
