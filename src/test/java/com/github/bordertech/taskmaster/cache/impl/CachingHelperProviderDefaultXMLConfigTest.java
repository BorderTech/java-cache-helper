package com.github.bordertech.taskmaster.cache.impl;

import com.github.bordertech.config.Config;
import javax.cache.Cache;
import javax.cache.Caching;
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
	public void testXMLConfigValidCache() {
		Cache<String, Integer> cache = provider.getOrCreateCache("testCache", String.class, Integer.class);
		Assert.assertEquals("Invalid cache with cache configured from XML", "testCache", cache.getName());
	}

	@Test(expected = ClassCastException.class)
	public void testXMLConfigDefinedCacheWrongTypes() {
		// Cache types do not match the XML config so should throw exception
		provider.getOrCreateCache("testCache", Long.class, Long.class);
	}

	@Test
	public void testXMLConfigCacheNotDefined() {
		Cache<String, Integer> cache = provider.getOrCreateCache("testCache2", String.class, Integer.class);
		Assert.assertEquals("Invalid cache with cache not in XML config", "testCache2", cache.getName());
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
