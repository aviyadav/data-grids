package com.sample.datagrid.ignite.adapter;

import com.sample.datagrid.ignite.model.Orders;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.cache.Cache;
import javax.cache.integration.CacheLoaderException;
import javax.cache.integration.CacheWriterException;
import org.apache.ignite.cache.store.CacheStoreAdapter;
import org.apache.ignite.cache.store.CacheStoreSession;
import org.apache.ignite.resources.CacheStoreSessionResource;
import org.jetbrains.annotations.Nullable;

public class OrdersCacheStoreAdapter extends CacheStoreAdapter<Long, Orders> {
    
    private static final String CONN_NAME = "CONN_STORE";
    
    @CacheStoreSessionResource
    private CacheStoreSession cacheStoreSession;

    @Override
    public Orders load(Long key) throws CacheLoaderException {
        return null;
    }

    @Override
    public void write(Cache.Entry<? extends Long, ? extends Orders> entry) throws CacheWriterException {
        Long key = entry.getKey();
        Orders value = entry.getValue();
        
        System.out.println("INFO: Inserting the record for order#:" + key);
        Connection conn = null;
        
        try {
            conn = connection();
            
            PreparedStatement stOrder, stOrderLine;
            
            // Delete the row if any from the orderlines table for the current key
            stOrder = conn.prepareStatement("delete from orders where order_number = ?");
            stOrder.setLong(1, value.getOrderNumber());
            stOrder.executeUpdate();
            
            stOrderLine = conn.prepareStatement("delete from order_line where order_number = ?");
            stOrderLine.setLong(1, value.getOrderNumber());
            stOrderLine.executeUpdate();
            
            // Insert the rows into table
            stOrder = conn.prepareStatement("insert into orders (order_number, order_type, order_fulfillment_date) values (?, ?, ?)");
            stOrder.setLong(1, value.getOrderNumber());
            stOrder.setString(2, value.getOrderType());
            stOrder.setDate(3, value.getOrderFulfillmentDate());
            stOrder.executeUpdate();
            
            stOrderLine = conn.prepareStatement("insert into order_line (order_number, order_line_number, item_name,item_qty) values (?, ?, ?,?)");
            for (int i = 0; i < value.getOrderLine().length; i++) {
                // https://iteritory.com/apache-ignite-write-through-database-caching/
                // https://iteritory.com/apache-ignite-read-through-database-caching/
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrdersCacheStoreAdapter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        // TODO
    }

    private Connection connection() throws SQLException {
        if(cacheStoreSession.isWithinTransaction()) {
            System.out.println("INFO:The cache store session is within Transaction");
            Map<Object, Object> connectionProperties = cacheStoreSession.properties();
            
            Connection conn = (Connection) connectionProperties.get(CONN_NAME);
            if(conn == null) {
                System.out.println("INFO:Connection does not exist; create a new connection with autoCommitFlag as False");
                conn = openConnection(false);
                connectionProperties.put(CONN_NAME, conn);
            } else {
                System.out.println("INFO:Connection exists; we'll reuse the same connection");
            }
            
            return conn;
        } else {
            System.out.println("INFO:The cache store session is NOT within Transaction; create a new connection");
            return openConnection(true);
        }
    }

    private Connection openConnection(boolean autoCommitFlag) throws SQLException {
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/ignite_demo?user=ignite_user&password=password");
        conn.setAutoCommit(autoCommitFlag);
        System.out.println("INFO:Connection object is created with autoCommitFlag as:" + autoCommitFlag);
        return conn;
    }
    
    private void endConnection(@Nullable Connection conn) {
        if(!cacheStoreSession.isWithinTransaction() && conn != null) {
            try {
                conn.close();
                System.out.println("INFO:Connection object is closed");
            } catch (SQLException ex) {
                Logger.getLogger(OrdersCacheStoreAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
