package org.sample.cache.ignite;

import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cluster.ClusterGroup;

import java.util.Arrays;
import java.util.Collection;

public class HelloWorld {
    public static void main(String[] args) {
        try (Ignite ignite = Ignition.start("C:\\Users\\Q845332\\codebase\\sw\\apache-ignite-fabric-2.6.0\\examples\\config\\example-ignite.xml")) {
/*            IgniteCache<Integer, String> cache = ignite.getOrCreateCache("myCache");
            cache.put(1, "Hello");
            cache.put(2, "World!");

            ignite.compute().broadcast(() -> System.out.println(cache.get(1) + " " + cache.get(2)));
*/

/*            ClusterGroup rmts = ignite.cluster().forRemotes();
            ignite.compute(rmts).broadcast(() -> System.out.println("Hello Remote"));
*/

            Collection<Integer> res = ignite.compute().apply(
                    (String word) -> {
                        System.out.println("Counting characters in word '" + word + "'");
                        return word.length();
                    }, Arrays.asList("How many characters in this sample".split(" "))
            );

            int total = res.stream().mapToInt(Integer::intValue).sum();

            System.out.println("Total characters - " + total);
        }
    }
}
