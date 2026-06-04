package fiap.com.br.orbitpasscore.common.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

/**
 * Enables Spring's annotation-driven cache management (@Cacheable, @CacheEvict, ...).
 *
 * <p>No cache provider is on the classpath, so Spring Boot falls back to a simple
 * in-memory {@code ConcurrentMapCacheManager}. That is fine for a single instance;
 * swap in Redis/Caffeine later if a shared or evicting cache is needed.</p>
 */
@Configuration
@EnableCaching
public class CacheConfig {
}
