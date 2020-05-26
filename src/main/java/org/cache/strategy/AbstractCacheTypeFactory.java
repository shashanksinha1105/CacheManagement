package org.cache.strategy;

import org.cache.CacheTypeStrategy;

public abstract class AbstractCacheTypeFactory {
    abstract CacheTypeStrategy getCacheType(String name);
}
