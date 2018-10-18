package com.sample.datagrid.ignite.ignitetutorial;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;

public class FirstProgram {
    
    public static void main(String[] args) {
        Ignition.setClientMode(true);
        
        Ignite ignite = Ignition.start("C:\\Users\\Q845332\\codebase\\data\\temp\\config\\itc-poc-config.xml");
        
        
        IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myFirstIgniteCache");
        cache.put(1, "A1111");
        cache.put(2, "B2222");
        cache.put(3, "C3333");
        cache.put(4, "D4444");
        
        System.out.println(cache.get(1));
        System.out.println(cache.get(2));
        System.out.println(cache.get(3));
        System.out.println(cache.get(4));
    }
}
