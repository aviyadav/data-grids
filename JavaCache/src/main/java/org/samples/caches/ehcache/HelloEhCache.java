package org.samples.caches.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

public class HelloEhCache {

    public static void main(String[] args) {
        
//        CacheManager cm = CacheManager.getInstance();
        CacheManager cm = CacheManager.newInstance();

//        cm.addCache("cache1");
               
        Cache cache = cm.getCache("cache1");
        
        cache.put(new Element("1", "Jan"));
        cache.put(new Element("2", "Feb"));
        cache.put(new Element("3", "Mar"));
        cache.put(new Element("4", "Apr"));
        cache.put(new Element("5", "May"));
        cache.put(new Element("6", "Jun"));
        cache.put(new Element("7", "Jly"));
        cache.put(new Element("8", "Aug"));
        cache.put(new Element("9", "Sep"));
        cache.put(new Element("10", "Oct"));
        cache.put(new Element("11", "Nov"));
        cache.put(new Element("12", "Dec"));
        
        Element ele = cache.get("1");
        String output = (ele == null ? null : ele.getObjectValue().toString());
        System.out.println("Output -- " + output);
        
        System.out.println(cache.isKeyInCache("1"));
        System.out.println(cache.isKeyInCache("5"));
        System.out.println(cache.isKeyInCache("19"));
        
        cm.shutdown();
    }
}
