package org.cache;

import org.cache.strategy.ConnectionCredentials;

public interface CacheTypeStrategy {
    CacheTypeStrategy connect(ConnectionCredentials connectionCredentials);
    void put(String parentKey, String key, String value);
    void put(String parentKey, String key, Object value);
    <T> T get(String parentKey, String key, Class<T> classReturn);
    String get(String parentKey, String key);
    void delete(String parentKey, String key) ;


}
