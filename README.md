# Gradle HTTP Build Cache Server

[![Build Status](https://travis-ci.org/sinwe/http-gradle-cache-server.svg?branch=master)](https://travis-ci.org/sinwe/http-gradle-cache-server)

This is an alternative Gradle build cache server.
There are 2 mode that this cache server can be used:
* Persisted
* In Memory

## Persisted Mode
Store cache into disk. At this moment the maximum cache size is 64GB, enough for most build.

What it does:
* Cache gradle build
* Persist into disk so it survives server being restarted

What it does not do:
* No eviction strategy

## In Memory Mode (Default)
Store cache into memory. The total cache entry stored in the cache is limited by the heap allocated to JVM

What it does:
* Cache gradle build
* Store in memory

Option for this mode:
* Replicate to another node if configured
* Evict entries to free up memory using LRU
