package br.com.alysongustavo.policymanagementservice.infra.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.cache.RedisCacheConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = {CacheConfig.class, CacheConfigTest.MockCacheConfig.class})
public class CacheConfigTest {

    @TestConfiguration
    static class MockCacheConfig {
        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager();
        }
    }

    @Autowired
    private ApplicationContext context;

    @Autowired
    private RedisCacheConfiguration redisCacheConfiguration;

    @Test
    @DisplayName("Should load application context and register RedisCacheConfiguration bean")
    void shouldLoadContextAndRegisterCacheConfigBean() {
        assertThat(context.containsBean("redisCacheConfiguration")).isTrue();
        assertThat(redisCacheConfiguration).isNotNull();
    }

}