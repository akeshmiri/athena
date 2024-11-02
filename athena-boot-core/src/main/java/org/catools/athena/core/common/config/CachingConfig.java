package org.catools.athena.core.common.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
@EnableCaching
@SuppressWarnings("unused")
public class CachingConfig {

  @Bean
  public CacheManager cacheManager() {
    return new ConcurrentMapCacheManager("userByUsername", "userByUsernameOrAlias", "projectByCode");
  }

  @CacheEvict(value = {"userByUsername", "userByUsernameOrAlias", "projectByCode"}, allEntries = true)
  @Scheduled(fixedRate = 60 * 1000)
  public void emptyCache() {
    // this is a scheduled timer to clean up caches
  }
}