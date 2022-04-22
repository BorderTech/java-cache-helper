package com.github.bordertech.taskmaster.cache;

import com.caffinc.statiflex.Statiflex;
import java.util.concurrent.TimeUnit;
import javax.cache.configuration.Configuration;
import javax.cache.expiry.Duration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link CachingHelper}.
 */
public class CachingHelperTest {

	private static final String PROVIDER_FIELD_NAME = "PROVIDER";

	private CachingHelperProvider defaultProvider;
	private CachingHelperProvider mockProvider;

	@Before
	public void setup() {
		// Save original provider implementation from static final field before overriden with Mock in tests
		defaultProvider = CachingHelper.getProvider();
		// Override default provider with Mock
		mockProvider = setupMockProvider();
	}

	@After
	public void teardown() throws NoSuchFieldException {
		// Reset provider
		Statiflex.flex(CachingHelper.class, PROVIDER_FIELD_NAME, defaultProvider);
		mockProvider = null;
	}

	@Test
	public void checkGetProvider() {
		Assert.assertSame("Mock provider should have been returned", mockProvider, CachingHelper.getProvider());
		// Check no methods called on provider
		Mockito.verifyNoInteractions(mockProvider);
	}

	@Test
	public void checkCloseCacheManager() {
		CachingHelper.closeCacheManager();
		// Check close manager was called on provider
		Mockito.verify(mockProvider).closeCacheManager();
	}

	@Test
	public void checkGetCache() {
		CachingHelper.getOrCreateCache("mycache", String.class, Integer.class);
		// Check provider called correctly
		Mockito.verify(mockProvider).getOrCreateCache("mycache", String.class, Integer.class);
	}

	@Test
	public void checkGetCacheWithDuration() {
		Duration duration = new Duration(TimeUnit.MINUTES, 100);
		CachingHelper.getOrCreateCache("mycache", String.class, Integer.class, duration);
		// Check provider called correctly
		Mockito.verify(mockProvider).getOrCreateCache("mycache", String.class, Integer.class, duration);
	}

	@Test
	public void checkGetCacheWithConfiguration() {
		Configuration<String, Integer> config = Mockito.mock(Configuration.class);
		CachingHelper.getOrCreateCache("mycache", String.class, Integer.class, config);
		// Check provider called correctly
		Mockito.verify(mockProvider).getOrCreateCache("mycache", String.class, Integer.class, config);
	}

	private CachingHelperProvider setupMockProvider() {
		try {
			// Override default provider
			CachingHelperProvider provider = Mockito.mock(CachingHelperProvider.class);
			Statiflex.flex(CachingHelper.class, PROVIDER_FIELD_NAME, provider);
			return provider;
		} catch (NoSuchFieldException ex) {
			throw new IllegalStateException("Could not setup mock provider", ex);
		}
	}

}
