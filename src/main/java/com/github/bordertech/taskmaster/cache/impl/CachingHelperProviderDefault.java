package com.github.bordertech.taskmaster.cache.impl;

import com.github.bordertech.taskmaster.cache.CachingHelperProvider;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import javax.cache.Cache;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.Configuration;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.AccessedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.inject.Singleton;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default CachingHelperProvider implementation using JSR107 provider.
 * <p>
 * This helper will check for a XML config file ("tm-cache.xml") at the top level of the classpath. Refer to
 * {@link CachingProperties#getConfigXmlLocation()} on overriding this file name and location. If this XML file is not found then the helper will use
 * the default cache manager.
 * </p>
 *
 * @see <a href="https://jcp.org/aboutJava/communityprocess/implementations/jsr107/index.html">JSR107 Implementations</a>
 * @see <a href="https://www.ehcache.org/documentation/3.0/107.html">Ehcache JSR107 provider details</a>
 * @see <a href="https://www.ehcache.org/documentation/3.0/xml.html">Ehcache JSR107 XML Configuration details</a>
 * @see CachingProperties
 */
@Singleton
public class CachingHelperProviderDefault implements CachingHelperProvider {

	private static final Log LOGGER = LogFactory.getLog(CachingHelperProviderDefault.class);

	private final CacheManager cacheManager;

	/**
	 * Setup the backing cache manager.
	 */
	public CachingHelperProviderDefault() {
		// Check if XML config is in the classpath
		URI uri = checkXMLConfig();
		if (uri == null) {
			// Use default manager
			cacheManager = Caching.getCachingProvider().getCacheManager();
		} else {
			// Use XML config
			cacheManager = Caching.getCachingProvider().getCacheManager(uri, CachingHelperProviderDefault.class.getClassLoader());
		}
	}

	/**
	 * @return the XML cache config URI or null if not available
	 */
	protected final URI checkXMLConfig() {
		// Check cache XML config file location runtime parameter (default tm-cache.xml)
		String config = CachingProperties.getConfigXmlLocation();
		if (StringUtils.isBlank(config)) {
			LOGGER.info("No XML cache config property set. Will use cache provider default config.");
			return null;
		}
		// Check if XML config file exists on classpath
		try {
			URL url = CachingHelperProviderDefault.class.getResource(config);
			if (url == null) {
				LOGGER.info("Could not find XML cache config [" + config + "] on classpath. Will use cache provider default config.");
				return null;
			}
			LOGGER.info("XML cache config [" + config + "] found on classpath and will be used for the cache manager config.");
			return url.toURI();
		} catch (URISyntaxException e) {
			throw new IllegalStateException("Could not load XML cache config [" + config + "]. " + e.getMessage(), e);
		}
	}

	@Override
	public CacheManager getCacheManager() {
		return cacheManager;
	}

	@Override
	public synchronized void closeCacheManager() {
		if (!getCacheManager().isClosed()) {
			getCacheManager().close();
		}
	}

	@Override
	public synchronized <K, V> Cache<K, V> getOrCreateCache(final String name, final Class<K> keyClass, final Class<V> valueClass) {
		Cache<K, V> cache = getCacheManager().getCache(name, keyClass, valueClass);
		if (cache == null) {
			// Get cache duration
			Duration duration = CachingProperties.getCacheDuration(name);
			cache = createCache(name, keyClass, valueClass, duration);
		}
		return cache;
	}

	@Override
	public synchronized <K, V> Cache<K, V> getOrCreateCache(final String name, final Class<K> keyClass,
			final Class<V> valueClass, final Duration duration) {
		Cache<K, V> cache = getCacheManager().getCache(name, keyClass, valueClass);
		if (cache == null) {
			// Check for duration override
			Duration durationOverride = CachingProperties.getCacheDuration(name, duration);
			cache = createCache(name, keyClass, valueClass, durationOverride);
		}
		return cache;
	}

	@Override
	public synchronized <K, V> Cache<K, V> getOrCreateCache(final String name, final Class<K> keyClass,
			final Class<V> valueClass, final Configuration<K, V> config) {
		Cache<K, V> cache = getCacheManager().getCache(name, keyClass, valueClass);
		if (cache == null) {
			cache = getCacheManager().createCache(name, config);
		}
		return cache;
	}

	/**
	 * Create a cache by setting up the cache config with the provided key and entry classes and duration.
	 *
	 * @param <K> the key type
	 * @param <V> the value type
	 * @param name the cache name
	 * @param keyClass the key class type
	 * @param valueClass the value class type
	 * @param duration the cache duration amount
	 * @return the cache
	 */
	protected synchronized <K, V> Cache<K, V> createCache(final String name, final Class<K> keyClass,
			final Class<V> valueClass, final Duration duration) {
		MutableConfiguration<K, V> config = new MutableConfiguration<>();
		config.setTypes(keyClass, valueClass);
		config.setExpiryPolicyFactory(AccessedExpiryPolicy.factoryOf(duration));
		return getCacheManager().createCache(name, config);
	}

}
