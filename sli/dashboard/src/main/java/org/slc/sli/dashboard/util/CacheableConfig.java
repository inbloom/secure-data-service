package org.slc.sli.dashboard.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.cache.annotation.Cacheable;

/**
 *
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
@Cacheable(value = Constants.CACHE_USER_PANEL_CONFIG, key = "#edOrgKey.hashCode() + #componentId.hashCode() * 31")
public @interface CacheableConfig {
}