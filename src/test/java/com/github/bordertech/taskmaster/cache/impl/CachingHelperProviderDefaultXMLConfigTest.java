package com.github.bordertech.taskmaster.cache.impl;

import com.github.bordertech.config.Config;
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
public class CachingHelperProviderDefaultXMLConfigTest {

	private static final String CONFIG_KEY = "bordertech.taskmaster.cache.config";

	private CachingHelperProviderDefault provider;

	@Before
	public void setup() {
		// Setup provider
		Config.getInstance().addProperty(CONFIG_KEY, "/test-cache-config.xml");
		provider = new CachingHelperProviderDefault();
	}

	@After
	public void tearDown() {
		// Close manager
		provider.closeCacheManager();
		// Reload parameters
		Config.reset();
	}

	@Test
	public void testGetCacheManagerNotDefault() {
		Assert.assertNotSame("Backing cache manager should not be the default manager", Caching.getCachingProvider().getCacheManager(), provider.getCacheManager());
	}

	@Test
	public void testGetOrCreateCache() {
		Cache<String, Integer> cache = provider.getOrCreateCache("testCache", String.class, Integer.class);
		Assert.assertEquals("Invalid cache from XML config", "testCache", cache.getName());
	}

	@Test
	public void testGetorCreateCacheWithDuration() {
		Duration duration = new Duration(TimeUnit.MINUTES, 1000);
		Cache<String, Integer> cache = provider.getOrCreateCache("testCache2", String.class, Integer.class, duration);
		Assert.assertEquals("Invalid cache with duration from XML config", "testCache2", cache.getName());
	}

	@Test
	public void testGetOrCreateCacheWithConfiguration() {
		Configuration<String, Integer> config = new MutableConfiguration();
		Cache<String, Integer> cache = provider.getOrCreateCache("testCache3", String.class, Integer.class, config);
		Assert.assertEquals("Invalid cache with config from XML config", "testCache3", cache.getName());
	}

	@Test
	public void testGetOrCreateCacheNotDefined() {
		Cache<String, Integer> cache = provider.getOrCreateCache("testCache4", String.class, Integer.class);
		Assert.assertEquals("Invalid cache with cache not defined in XML config", "testCache4", cache.getName());
	}

	@Test(expected = ClassCastException.class)
	public void testCacheDefinedWrongTypes() {
		// Cache types do not match the XML config so should throw exception
		provider.getOrCreateCache("testCache5", Long.class, Long.class);
	}

	@Test
	public void testXMLConfigDoesNotExistOnClasspath() {
		Config.getInstance().clearProperty(CONFIG_KEY);
		Config.getInstance().addProperty(CONFIG_KEY, "/not-exist.xml");
		Assert.assertNull("XML Config should be null as file not on classpath", provider.checkXMLConfig());
	}

	@Test
	public void testXMLConfigParameterIsBlank() {
		Config.getInstance().clearProperty(CONFIG_KEY);
		Config.getInstance().addProperty(CONFIG_KEY, "");
		Assert.assertNull("XML Config should be null as runtime parameter is blank", provider.checkXMLConfig());
	}

}
