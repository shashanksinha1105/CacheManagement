package org.cache.strategy;

import org.cache.CacheTypeStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class for returning the CacheTypeFactory on the basis of the name mentioned.
 * @author shashank
 */
public class CacheTypeFactory extends AbstractCacheTypeFactory {
    private static Map<String, CacheTypeStrategy> caches = new ConcurrentHashMap<>();
    private static Map<CacheTypeStrategy, Object> connectionCache = new ConcurrentHashMap<>();

    @Override
    public CacheTypeStrategy getCacheType(String name) {
        CacheTypeStrategy cacheTypeStrategy = null;
        if(caches.containsKey(name)) {
            return caches.get(name);
        }
        if(Constants.REDIS.equals(name)) {
            cacheTypeStrategy = new RedisCacheStrategy();
        } else if(Constants.AEROSPIKE.equals(name)) {
            cacheTypeStrategy = new AerospikeCacheStrategy();
        }
        return loadCaches(cacheTypeStrategy, name);
    }

    private CacheTypeStrategy loadCaches(CacheTypeStrategy cacheTypeStrategy, String name) {
        caches.put(name, cacheTypeStrategy); // Redis,
        return cacheTypeStrategy;
    }

    public static void loadConnectionCache(String connectionType, Object connection) {
        connectionCache.put(caches.get(connectionType), connection);
    }
}
