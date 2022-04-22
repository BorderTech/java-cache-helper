package com.github.bordertech.taskmaster.cache.servlet;

import com.caffinc.statiflex.Statiflex;
import com.github.bordertech.taskmaster.cache.CachingHelper;
import com.github.bordertech.taskmaster.cache.CachingHelperProvider;
import javax.servlet.ServletContextEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link CachingProviderListener}.
 */
public class CachingProviderListenerTest {

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
	public void checkContextDestroyed() {

		CachingProviderListener listener = new CachingProviderListener();
		ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
		listener.contextDestroyed(sce);

		// Check close manager was called on provider
		Mockito.verify(mockProvider).closeCacheManager();
	}

	@Test
	public void checkContextInitialised() {

		CachingProviderListener listener = new CachingProviderListener();
		ServletContextEvent sce = Mockito.mock(ServletContextEvent.class);
		listener.contextInitialized(sce);

		// Check no methods called on provider
		Mockito.verifyNoInteractions(mockProvider);
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
