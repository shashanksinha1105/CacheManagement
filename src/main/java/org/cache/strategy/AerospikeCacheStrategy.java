package org.cache.strategy;

import com.aerospike.client.*;
import com.aerospike.client.policy.ClientPolicy;
import com.aerospike.client.policy.WritePolicy;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.cache.CacheTypeStrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AerospikeCacheStrategy implements CacheTypeStrategy {
    private AerospikeClient aerospikeClient;
    private String namespace;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private Object connection;

    public CacheTypeStrategy connect(ConnectionCredentials connectionCredentials) {
        Host[] hosts = new Host[]{
                new Host(connectionCredentials.getHosts().get(0), connectionCredentials.getPorts().get(0))
        };
        ClientPolicy policy = new ClientPolicy();
        aerospikeClient = new AerospikeClient(policy, hosts);
        this.namespace = connectionCredentials.getNamespace();
        CacheTypeFactory.loadConnectionCache("aerospike", aerospikeClient);
        this.connection = aerospikeClient;
        return this;
    }

    public void put(String set, String key, Object value) {
        Key dataKey = new Key(this.namespace, set, key);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, Object> mapData = objectMapper.convertValue(value, Map.class);
        List<Bin> binList = new ArrayList<Bin>();
        WritePolicy writePolicy = null;

        Set<String> keys = mapData.keySet();
        for (String keyMap : keys) {
            Bin bin = null;
            if (mapData.get(keyMap) != null) {
                bin = new Bin(keyMap, mapData.get(keyMap));
                binList.add(bin);
            }
        }
        aerospikeClient.put(writePolicy, dataKey, binList.toArray(new Bin[binList.size()]));

    }

    public void put(String parentKey, String key, String value) {
        Key dataKey = new Key(this.namespace, parentKey, key);
        Bin binValue = new Bin("value", value);
        aerospikeClient.put(null, dataKey, binValue);
    }


    public <T> T get(String parentKey, String key, Class<T> returnClass) {
        Key key1 = new Key(this.namespace, parentKey, key);
        Record record = aerospikeClient.get(null, key1);
        Map<String, Object> map = record.bins;
        if(map != null)
            return OBJECT_MAPPER.convertValue(map, returnClass);
        return null;
    }

    public String get(String parentKey, String key) {
        Key key1 = new Key(this.namespace, parentKey, key);
        Record record = aerospikeClient.get(null, key1);
        Map<String, Object> map = record.bins;
        if(map != null) {
            return (String) map.get("value");
        }
        return null;
    }

    public void delete(String parentKey, String key) {
        Key key1 = new Key(this.namespace, parentKey, key);
        aerospikeClient.delete(null, key1);
    }
}