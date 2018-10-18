package org.sample.datagrid.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;

public class HelloWorld {
    
    public static void main(String[] args) {
        
        Ignite ignite = Ignition.start("C:\\Users\\Q845332\\codebase\\sw\\apache-ignite-fabric-2.6.0\\examples\\config\\example-ignite.xml");
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("sampleCache");
        
        cache.put(1, "Hello");
        cache.put(2, "World!");
        
        ignite.compute().broadcast(() -> {
            String _first = cache.get(1);
            String _second = cache.get(2);
            
            System.out.println(_first + " " + _second);
        });
    }
}
