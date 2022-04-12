package com.github.bordertech.taskmaster.cache.impl;

import com.github.bordertech.config.Config;
import java.util.concurrent.TimeUnit;
import javax.cache.expiry.Duration;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit tests for {@link CachingProperties}.
 */
public class CachingPropertiesTest {

	private static final String DEFAULT_UNIT_KEY = "bordertech.taskmaster.cache.default.unit";
	private static final String DEFAULT_AMOUNT_KEY = "bordertech.taskmaster.cache.default.amount";
	private static final String MY_CACHE_NAME = "MYCACHE";
	private static final String MY_CACHE_UNIT_KEY = "bordertech.taskmaster.cache.MYCACHE.duration.unit";
	private static final String MY_CACHE_AMOUNT_KEY = "bordertech.taskmaster.cache.MYCACHE.duration.amount";

	private static final Duration EXPECTED_DEFAULT_DURATION = new Duration(TimeUnit.SECONDS, 1800);

	@After
	public void teardown() {
		Config.reset();
	}

	@Test
	public void configXmlLocationDefault() {
		Assert.assertEquals("Invalid default XML config location", "/tm-cache.xml", CachingProperties.getConfigXmlLocation());
	}

	@Test
	public void configXmlLocationOverride() {
		Config.getInstance().addProperty("bordertech.taskmaster.cache.config", "test.xml");
		Assert.assertEquals("Invalid override XML config location", "test.xml", CachingProperties.getConfigXmlLocation());
	}

	@Test
	public void testDefaultDuration() {
		Duration duration = CachingProperties.getDefaultDuration();
		Assert.assertEquals("Invalid default duration", EXPECTED_DEFAULT_DURATION, duration);
	}

	@Test
	public void testDefaultDurationOverride() {
		Config.getInstance().addProperty(DEFAULT_UNIT_KEY, "m");
		Config.getInstance().addProperty(DEFAULT_AMOUNT_KEY, 1000);
		Duration expected = new Duration(TimeUnit.MINUTES, 1000);
		Duration duration = CachingProperties.getDefaultDuration();
		Assert.assertEquals("Invalid override default duration", expected, duration);
	}

	@Test
	public void testDefaultDurationTimeUnitDays() {
		Config.getInstance().addProperty(DEFAULT_UNIT_KEY, "d");
		Duration duration = CachingProperties.getDefaultDuration();
		Assert.assertEquals("Default duration unit should be days", TimeUnit.DAYS, duration.getTimeUnit());
	}

	@Test
	public void testDefaultDurationTimeUnitHours() {
		Config.getInstance().addProperty(DEFAULT_UNIT_KEY, "h");
		Duration duration = CachingProperties.getDefaultDuration();
		Assert.assertEquals("Default duration unit should be hours", TimeUnit.HOURS, duration.getTimeUnit());
	}

	@Test
	public void testDefaultDurationTimeUnitMinutes() {
		Config.getInstance().addProperty(DEFAULT_UNIT_KEY, "m");
		Duration duration = CachingProperties.getDefaultDuration();
		Assert.assertEquals("Default duration unit should be minutes", TimeUnit.MINUTES, duration.getTimeUnit());
	}

	@Test
	public void testDefaultDurationTimeUnitSeconds() {
		Config.getInstance().addProperty(DEFAULT_UNIT_KEY, "s");
		Duration duration = CachingProperties.getDefaultDuration();
		Assert.assertEquals("Default duration unit should be seconds", TimeUnit.SECONDS, duration.getTimeUnit());
	}

	@Test
	public void testDefaultDurationTimeUnitMilliSeconds() {
		Config.getInstance().addProperty(DEFAULT_UNIT_KEY, "mi");
		Duration duration = CachingProperties.getDefaultDuration();
		Assert.assertEquals("Default duration unit should be milli seconds", TimeUnit.MILLISECONDS, duration.getTimeUnit());
	}

	@Test
	public void testDefaultDurationTimeUnitUndefined() {
		Config.getInstance().addProperty(DEFAULT_UNIT_KEY, "X");
		Duration duration = CachingProperties.getDefaultDuration();
		Assert.assertEquals("Default duration unit should be seconds when not defined", TimeUnit.SECONDS, duration.getTimeUnit());
	}

	@Test
	public void testCacheDefaultDuration() {
		Duration duration = CachingProperties.getCacheDuration(MY_CACHE_NAME);
		Assert.assertEquals("Invalid default cache duration", EXPECTED_DEFAULT_DURATION, duration);
	}

	@Test
	public void testCacheDefaultDurationOverride() {
		Config.getInstance().addProperty(MY_CACHE_UNIT_KEY, "m");
		Config.getInstance().addProperty(MY_CACHE_AMOUNT_KEY, 1000);
		Duration expected = new Duration(TimeUnit.MINUTES, 1000);
		Duration duration = CachingProperties.getCacheDuration(MY_CACHE_NAME);
		Assert.assertEquals("Invalid override cache duration", expected, duration);
	}

	@Test
	public void testCacheWithDuration() {
		Duration withDuration = new Duration(TimeUnit.HOURS, 2000);
		Duration duration = CachingProperties.getCacheDuration(MY_CACHE_NAME, withDuration);
		Assert.assertEquals("Invalid cache with duration", withDuration, duration);
	}

	@Test
	public void testCacheWithDurationAndOverride() {
		Config.getInstance().addProperty(MY_CACHE_UNIT_KEY, "m");
		Config.getInstance().addProperty(MY_CACHE_AMOUNT_KEY, 1000);
		Duration expected = new Duration(TimeUnit.MINUTES, 1000);
		Duration withDuration = new Duration(TimeUnit.HOURS, 2000);
		Duration duration = CachingProperties.getCacheDuration(MY_CACHE_NAME, withDuration);
		Assert.assertEquals("Invalid cache with duration and override", expected, duration);
	}

}
