package com.gmail.quiz.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;
import javax.cache.spi.CachingProvider;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableCaching
public class CachingConfig {

    @Bean
    JCacheCacheManager jCacheCacheManager() {
        return new JCacheCacheManager(cacheManager());
    }

    @Bean(destroyMethod = "close")
    CacheManager cacheManager() {

        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        MutableConfiguration<Object, Object> cacheConfiguration = new MutableConfiguration<>()
                .setTypes(Object.class, Object.class)
                .setStoreByValue(false)
                .setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.MINUTES, 5)));

        cacheManager.createCache("question", cacheConfiguration);

        return cacheManager;
    }
}