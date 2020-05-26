import org.cache.CacheTypeStrategy;
import org.cache.strategy.CacheTypeFactory;
import org.cache.strategy.ConnectionCredentials;
import org.cache.strategy.Result;
import org.cache.strategy.UserDataRequest;

import java.util.ArrayList;
import java.util.List;

/*
Application.java - Client
getCacheType("Redis"); -> RedisCacheStrategy
cacheTypeStragegy.connect(credentials);


1. retry policy
2. support
    multiget
    put - pojo, string, integer, boolean
 */
public class Application {

    public static void main(String[] args) {
        CacheTypeFactory cacheTypeFactory = new CacheTypeFactory();
        CacheTypeStrategy cacheTypeStrategy1 = cacheTypeFactory.getCacheType("redis");
        CacheTypeStrategy cacheTypeStrategy2 = cacheTypeFactory.getCacheType("aerospike");
        ConnectionCredentials connectionCredentials = new ConnectionCredentials();
        List<String> hosts = new ArrayList<String>();
        hosts.add("localhost");
        List<Integer> ports = new ArrayList<Integer>();
        ports.add(6379);
        connectionCredentials.setHosts(hosts);
        connectionCredentials.setPorts(ports);
        cacheTypeStrategy1 = cacheTypeStrategy1.connect(connectionCredentials);

        connectionCredentials.setHosts(hosts);
        ports.clear();
        ports.add(3000);
        connectionCredentials.setPorts(ports);
        connectionCredentials.setNamespace("test");
        cacheTypeStrategy2 = cacheTypeStrategy2.connect(connectionCredentials);

        UserDataRequest userDataRequest = new UserDataRequest();
        userDataRequest.setFirstName("jsi");
        userDataRequest.setLastName("shashank");

        cacheTypeStrategy1.put("infoedge","js", userDataRequest);
        cacheTypeStrategy2.put("infoedge","js", userDataRequest);

        userDataRequest = (UserDataRequest) cacheTypeStrategy1.get("infoedge", "js", Result.class);
        userDataRequest = (UserDataRequest) cacheTypeStrategy2.get("infoedge", "js", Result.class);
    }
}
