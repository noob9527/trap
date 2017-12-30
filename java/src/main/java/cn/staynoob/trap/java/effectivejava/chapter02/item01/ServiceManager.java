package cn.staynoob.trap.java.effectivejava.chapter02.item01;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ServiceManager {
    private ServiceManager() {
    }

    private static final Map<String, Provider> providers
            = new ConcurrentHashMap<>();

    private static final String DEFAULT_PROVIDER_NAME = "<def>";

    // Provider registration API
    public static void registerDefaultProvider(Provider provider) {
        registerProvider(DEFAULT_PROVIDER_NAME, provider);
    }

    public static void registerProvider(String name, Provider provider) {
        providers.put(name, provider);
    }

    public static Service newInstance() {
        return newInstance(DEFAULT_PROVIDER_NAME);
    }

    public static Service newInstance(String name) {
        Provider p = providers.get(name);
        if (p == null)
            throw new IllegalArgumentException(
                    "No provider registered with name: " + name
            );
        return p.newService();
    }
}
