# Cache Helper

## Status

[![Build Status](https://github.com/BorderTech/java-cache-helper/actions/workflows/github-actions-build.yml/badge.svg)](https://github.com/BorderTech/java-cache-helper/actions/workflows/github-actions-build.yml)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=BorderTech_java-cache-helper&metric=alert_status)](https://sonarcloud.io/summary/new_code?id=BorderTech_java-cache-helper)
[![Reliability Rating](https://sonarcloud.io/api/project_badges/measure?project=BorderTech_java-cache-helper&metric=reliability_rating)](https://sonarcloud.io/summary/new_code?id=BorderTech_java-cache-helper)
[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=BorderTech_java-cache-helper&metric=coverage)](https://sonarcloud.io/summary/new_code?id=BorderTech_java-cache-helper)
[![Javadocs](https://www.javadoc.io/badge/com.github.bordertech.taskmaster/cache-helper.svg)](https://www.javadoc.io/doc/com.github.bordertech.taskmaster/cache-helper)
[![Maven Central](https://img.shields.io/maven-central/v/com.github.bordertech.taskmaster/cache-helper.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22com.github.bordertech.taskmaster%22%20AND%20a:%22cache-helper%22)

## Content

- [What is Cache Helper](#what-is-cache-helper)
- [Why use Cache Helper](#why-use-cache-helper)
- [Getting started](#getting-started)
- [Configuration](#configuration)
- [Contributing](#contributing)

## What is Cache Helper

Provides projects using [JSR107](https://github.com/jsr107/jsr107spec) caches a central mechanism for creating their cache requirements.

## Why use Cache Helper

Cache Helper provides an easy way to control and configure caches.

Caches can be configured via runtime properties, the CachingHelper API or a predefined XML configuration file.

## Getting started

Add Cache Helper dependency:

``` xml
<project>
  ....
  <dependency>
    <groupId>com.github.bordertech.taskmaster</groupId>
    <artifactId>cache-helper</artifactId>
    <version>1.0.0</version>
  </dependency>
  ....
</project>
```

Add a [JSR107 cache implementation](https://jcp.org/aboutJava/communityprocess/implementations/jsr107/index.html) (such as [ehcache](https://www.ehcache.org)):

``` xml
<project>
  ....
  <dependency>
     <groupId>org.ehcache</groupId>
     <artifactId>ehcache</artifactId>
     <version>LATEST</version>
  </dependency>
  ....
</project>
```

[CachingHelper](https://github.com/BorderTech/java-cache-helper/blob/main/src/main/java/com/github/bordertech/taskmaster/cache/CachingHelper.java) is the central access point to the caching configuration mechanism.

To retrieve a cache called "mycache" with a String Key and Integer Entry, use the following code:

``` java
  ....
  Cache<String, Integer> cache = CachingHelper.getOrCreateCache("mycache", String.class, Integer.class);
  cache.put("mykey", 1);
  ....
```

The default duration used by Cache Helper is 1800 seconds.

To override the default duration via runtime properties, create a `bordertech-app.properties` file in the resources directory and add the following:

``` java
## Overriding default cache duration
bordertech.taskmaster.cache.default.unit=m
bordertech.taskmaster.cache.default.amount=15
```

To provide a specific duration for the "mycache" cache via runtime properties add the following properties:

``` java
## Override default to 1 hour for mycache
bordertech.taskmaster.cache.mycache.unit=h
bordertech.taskmaster.cache.mycache.amount=1
```

The duration for a cache can also be provided via the CachingHelper API:

``` java
  ....
  Cache<String, Integer> cache = CachingHelper.getOrCreateCache("mycache", String.class, Integer.class, new Duration(TimeUnit.MINUTES, 100));
  ....
```

If a duration is passed in via the API, and there is also a runtime property, the runtime property will take precedence.

If the JSR107 cache implementation supports XML configuration, then an XML configuration file named 'tm-cache.xml' can be included in the resources directory which Cache Helper will automatically detect and use to create the CacheManager.

To override the name or location of the XML configuration file use the following property:

``` java
## Override default XML config location
bordertech.taskmaster.cache.config=/my-file.xml
```

### Servlet Listener

Cache Helper also provides a Servlet Listener [CachingProviderListener](https://github.com/BorderTech/java-cache-helper/blob/main/src/main/java/com/github/bordertech/taskmaster/cache/servlet/CachingProviderListener.java) that can be added to a Servlet Context that will release caching resources when the context is destroyed.

## Configuration

The configuration of Cache Helper can be overridden by setting properties in a file `bordertech-app.properties` located in the resources directory.

Refer to [Config](https://github.com/BorderTech/java-config) for more details on how to set runtime properties.

The following properties can be set:-

|Property key|Description|Default value|
|-------------|-----------|-------------|
|bordertech.taskmaster.cache.default.unit|The default cache duration unit type|s|
|bordertech.taskmaster.cache.default.amount|The default cache duration value|1800|
|bordertech.taskmaster.cache.XXX.unit|The duration unit type for cache named XXX|-|
|bordertech.taskmaster.cache.XXX.amount|The duration value for cache named XXX|-|
|bordertech.taskmaster.cache.config|The optional XML configuration file location|\tm-cache.xml|

Possible duration unit type values:

|Unit Type Value|Description|
|-------------|-----------|
|d|Days|
|h|Hours|
|m|Minutes|
|s|Seconds|
|mi|Milli-seconds|

CachingHelper has a backing [CachingHelperProvider](https://github.com/BorderTech/java-cache-helper/blob/main/src/main/java/com/github/bordertech/taskmaster/cache/CachingHelperProvider.java) that handles the interfacing with the JSR107 Caching API.

To override the default CachingHelperProvider use the following property:

|Property key|Description|Default value|
|-------------|-----------|-------------|
|bordertech.factory.impl.com.github.bordertech.taskmaster.cache.CachingHelperProvider|The backing caching helper provider implementation.|com.github.bordertech.taskmaster.cache.impl.CachingHelperProviderDefault|

Refer to [Didums](https://github.com/BorderTech/didums) for more details on how the factory pattern is used for the CachingHelperProvider interface.

## Contributing

Refer to these guidelines for [Workflow](https://github.com/BorderTech/java-common/wiki/Workflow) and [Releasing](https://github.com/BorderTech/java-common/wiki/Releasing).
