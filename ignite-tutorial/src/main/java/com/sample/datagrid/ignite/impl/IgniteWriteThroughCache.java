package com.sample.datagrid.ignite.impl;

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
        Ignite ignite = Ignition.start("/Applications/programming/prog/nb/data-grid/ignite-tutorial/src/main/resources/config/itc-poc-config.xml");
        CacheConfiguration<Long, Orders> orderCacheConfig = new CacheConfiguration<>(ORDERS_CACHE);
        orderCacheConfig.setAtomicityMode(CacheAtomicityMode.TRANSACTIONAL);
        orderCacheConfig.setCacheStoreFactory(FactoryBuilder.factoryOf(OrdersCacheStoreAdapter.class));
        orderCacheConfig.setWriteThrough(true);

        IgniteCache<Long, Orders> cache = ignite.getOrCreateCache(orderCacheConfig);
        persistData(cache);
    }

    private static void persistData(IgniteCache<Long, Orders> cache) {
        try {
            Transaction tx = Ignition.ignite().transactions().txStart();
            System.out.println("START:Transaction started");

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

        } catch (ParseException ex) {
            Logger.getLogger(IgniteWriteThroughCache.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
