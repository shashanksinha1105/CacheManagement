package org.cache.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.cache.CacheTypeStrategy;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RedisCacheStrategy implements CacheTypeStrategy {
    JedisPool jedisPool = null;
    private Gson gson = new Gson();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private Object connection;
    public CacheTypeStrategy connect(ConnectionCredentials connectionCredentials) {
        final JedisPoolConfig poolConfig = buildPoolConfig();
        jedisPool = new JedisPool(poolConfig, connectionCredentials.getHosts().get(0));
        CacheTypeFactory.loadConnectionCache("redis", jedisPool);
        this.connection = jedisPool;
        return this;
    }

    private JedisPoolConfig buildPoolConfig() {
        final JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);
        poolConfig.setMaxIdle(128);
        poolConfig.setMinIdle(16);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);
        poolConfig.setNumTestsPerEvictionRun(3);
        poolConfig.setBlockWhenExhausted(true);
        return poolConfig;
    }

    /*
    for Object value :
    if value is type of string -save it as it is
    if value if type of POJO - extract the value and save
    in case of multiple keys of POJO - create a json with key values
     */
    public void put(String parentKey, String key, Object value) {
        String json = null;
        try {
            json = OBJECT_MAPPER.writeValueAsString(value);
            jedisPool.getResource().hset(parentKey, key,json);
        } catch (Exception e) {

        }

    }

    public void put(String parentKey, String key, String value) {
        jedisPool.getResource().hset(parentKey, key, value);
    }

    public <T> T get(String parentKey, String key, Class<T> returnClass) {
        String json = jedisPool.getResource().hget(parentKey, key);
        try {
            return OBJECT_MAPPER.readValue(json, returnClass);
        } catch (Exception e) {
            System.out.println("log exception :: " + e);
        }
        return null;
    }

    public String get(String parentKey, String key) {
        return jedisPool.getResource().hget(parentKey, key);
    }

    public void delete(String parentKey, String key) {
        try {
            jedisPool.getResource().hdel(parentKey, key);
        } catch (Exception e) {
            System.out.println("log exception :: " + e);
        }
    }
}
