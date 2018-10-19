package com.sample.datagrid.ignite.adapter;

import com.sample.datagrid.ignite.model.OrderLines;
import com.sample.datagrid.ignite.model.Orders;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
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

        Connection conn = null;
        PreparedStatement stOrderInfo, stOrderLineInfo;
        ResultSet rsOrder, rsOrderLines;
        Orders ord = null;
        List<OrderLines> ol;

        try {
            System.out.println("INFO: Cache Read through. The function is invoked as data is not available in cache.");
            conn = connection();
            stOrderInfo = conn.prepareStatement("select * from orders where order_number = ?");
            stOrderInfo.setLong(1, key);
            rsOrder = stOrderInfo.executeQuery();

            stOrderLineInfo = conn.prepareStatement("select * from order_line where order_number = ?");
            stOrderLineInfo.setLong(1, key);
            rsOrderLines = stOrderLineInfo.executeQuery();

            ol = new ArrayList<>();
            if (rsOrder.next()) {
                while (rsOrderLines.next()) {
                    ol.add(new OrderLines(rsOrderLines.getInt(1), rsOrderLines.getInt(2), rsOrderLines.getString(3), rsOrderLines.getInt(4)));
                }

                ord = new Orders(rsOrder.getInt(1), rsOrder.getString(2), rsOrder.getDate(3), ol);
            }
        } catch (SQLException ex) {
            Logger.getLogger(OrdersCacheStoreAdapter.class.getName()).log(Level.SEVERE, "Failed to load: " + key, ex);
        } finally {
            endConnection(conn);
        }
        return ord != null ? ord : null;
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

            for (OrderLines currentOrderLine : value.getOrderLine()) {
                stOrderLine.setInt(1, currentOrderLine.getOrderNumber());
                stOrderLine.setInt(2, currentOrderLine.getOrderLineNumber());
                stOrderLine.setString(3, currentOrderLine.getItemName());
                stOrderLine.setInt(4, currentOrderLine.getItemQty());
                stOrderLine.executeUpdate();
            }

        } catch (SQLException ex) {
            Logger.getLogger(OrdersCacheStoreAdapter.class.getName()).log(Level.SEVERE, "Failed to put object [key=" + key + ", val=" + value + "]", ex);
        } finally {
            endConnection(conn);
        }
    }

    @Override
    public void delete(Object key) throws CacheWriterException {
        // TODO
    }

    @Override
    public void sessionEnd(boolean commit) {
        Map<String, Connection> connectionProperties = cacheStoreSession.properties();
        try {
            Connection _con = connectionProperties.remove(CONN_NAME);
            if (_con != null) {
                if (commit) {
                    _con.commit();
                } else {
                    _con.rollback();
                }
            }
            System.out.println("END:Transaction ended successfully [commit=" + commit + ']');
        } catch (SQLException ex) {
            Logger.getLogger(OrdersCacheStoreAdapter.class.getName()).log(Level.SEVERE, "ERROR:Failed to end transaction: " + cacheStoreSession.transaction(), ex);
            throw new CacheWriterException("ERROR:Failed to end transaction: " + cacheStoreSession.transaction(), ex);
        }

    }

    private Connection connection() throws SQLException {
        if (cacheStoreSession.isWithinTransaction()) {
            System.out.println("INFO:The cache store session is within Transaction");
            Map<Object, Object> connectionProperties = cacheStoreSession.properties();

            Connection conn = (Connection) connectionProperties.get(CONN_NAME);
            if (conn == null) {
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
        if (!cacheStoreSession.isWithinTransaction() && conn != null) {
            try {
                conn.close();
                System.out.println("INFO:Connection object is closed");
            } catch (SQLException ex) {
                Logger.getLogger(OrdersCacheStoreAdapter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
