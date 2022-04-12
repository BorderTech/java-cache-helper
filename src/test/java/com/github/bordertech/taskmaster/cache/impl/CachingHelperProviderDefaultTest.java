package com.github.bordertech.taskmaster.cache.impl;

import java.util.concurrent.TimeUnit;
import javax.cache.Cache;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.Duration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit Tests for {@link CachingHelperProviderDefault}.
 */
public class CachingHelperProviderDefaultTest {

	private CachingHelperProviderDefault provider;

	@Before
	public void setup() {
		// Setup provider
		provider = new CachingHelperProviderDefault();
	}

	@After
	public void tearDown() {
		// Close manager
		provider.closeCacheManager();
	}

	@Test
	public void testGetCacheManager() {
		Assert.assertSame("Backing cache manager should be the default manager", Caching.getCachingProvider().getCacheManager(), provider.getCacheManager());
	}

	@Test
	public void testGetOrCreateCache() {
		Cache<String, Integer> cache = provider.getOrCreateCache("mycache1", String.class, Integer.class);
		Assert.assertEquals("Invalid cache name", "mycache1", cache.getName());
	}

	@Test
	public void testGetorCreateCacheWithDuration() {
		Duration duration = new Duration(TimeUnit.MINUTES, 1000);
		Cache<String, Integer> cache = provider.getOrCreateCache("mycache2", String.class, Integer.class, duration);
		Assert.assertEquals("Invalid cache with duration name", "mycache2", cache.getName());
	}

	@Test
	public void testGetOrCreateCacheWithConfiguration() {
		Configuration<String, Integer> config = new MutableConfiguration();
		Cache<String, Integer> cache = provider.getOrCreateCache("mycache3", String.class, Integer.class, config);
		Assert.assertEquals("Invalid cache with config name", "mycache3", cache.getName());
	}

}
