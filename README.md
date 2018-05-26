# Gradle HTTP Build Cache Server

This is an alternative Gradle build cache server that store cache into disk. At this moment the maximum cache size is 64GB, enough for most build.

What it does:
* Cache gradle build
* Persist into disk so it survives server being restarted

What it does not do:
* No eviction strategy
