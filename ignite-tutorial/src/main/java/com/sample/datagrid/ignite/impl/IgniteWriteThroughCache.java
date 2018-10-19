package com.sample.datagrid.ignite.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sample.datagrid.ignite.adapter.OrdersCacheStoreAdapter;
import com.sample.datagrid.ignite.model.OrderLines;
import com.sample.datagrid.ignite.model.Orders;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.cache.configuration.FactoryBuilder;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.cache.CacheAtomicityMode;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.transactions.Transaction;

public class IgniteWriteThroughCache {

    private static final String ORDERS_CACHE = "Orders_Cache";

    public static void main(String[] args) {
        Ignition.setClientMode(true);
        Ignite ignite = Ignition.start("C:\\Users\\Q845332\\codebase\\sw\\apache-ignite-fabric-2.6.0\\config\\itc-poc-config.xml");
        CacheConfiguration<Long, Orders> orderCacheConfig = new CacheConfiguration<>(ORDERS_CACHE);
        orderCacheConfig.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        orderCacheConfig.setCacheStoreFactory(FactoryBuilder.factoryOf(OrdersCacheStoreAdapter.class));

        orderCacheConfig.setReadThrough(true);
        orderCacheConfig.setWriteThrough(true);

        IgniteCache<Long, Orders> cache = ignite.getOrCreateCache(orderCacheConfig);
        persistData(cache);
    }

    private static void persistData(IgniteCache<Long, Orders> cache) {
        try {
            Transaction tx = Ignition.ignite().transactions().txStart();
            System.out.println("START:Write");

            OrderLines banana = new OrderLines(11, 1, "Banana", 12);
            OrderLines apple = new OrderLines(11, 2, "Apple", 6);

            OrderLines[] ol1 = new OrderLines[]{banana, apple};

            Date date1 = new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2017");
            java.sql.Date sDate = new java.sql.Date(date1.getTime());
            Orders order = new Orders(11, "EcomDeliveryOrder", sDate, ol1);
            cache.put((long) 11, order);

            OrderLines mosambi = new OrderLines(22, 1, "Mosambi", 3);
            OrderLines mango = new OrderLines(22, 2, "Mango", 4);
            OrderLines[] ol2 = new OrderLines[]{mosambi, mango};
            java.util.Date utilDate2 = new SimpleDateFormat("dd-MM-yyyy").parse("02-01-2017");
            java.sql.Date sqlDate2 = new java.sql.Date(utilDate2.getTime());
            Orders ord2 = new Orders(22, "StorePickupOrder", sqlDate2, ol2);
            cache.put((long) 22, ord2);

            tx.commit();

            System.out.println("END:Write");

        } catch (ParseException ex) {
            Logger.getLogger(IgniteWriteThroughCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private static void readData(IgniteCache<Long, Orders> cache) {

        //Transaction tx = Ignition.ignite().transactions().txStart();
        System.out.println("START:Read");
        Orders ord1 = cache.get((long) 11);
        Orders ord2 = cache.get((long) 22);
        Orders ord3 = cache.get((long) 33);
        ObjectMapper mapper = new ObjectMapper();

        try {
            //reading from cache
            String json = mapper.writeValueAsString(ord1);
            System.out.println("JSON (hits the cache) = " + json);

            //reading from cache
            json = mapper.writeValueAsString(ord2);
            System.out.println("JSON (hits the cache) = " + json);

            //order is not avialble in cache; reading from database
            json = mapper.writeValueAsString(ord3);
            System.out.println("JSON (hits the DB) = " + json);

            //After the first get call, order is now avialble in cache; reading from cache
            Orders ord4 = cache.get((long) 33);
            json = mapper.writeValueAsString(ord4);
            System.out.println("JSON (hits the cache) = " + json);
            System.out.println("END:Read");
        } catch (JsonProcessingException ex) {
            Logger.getLogger(IgniteWriteThroughCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
