package com.sample.datagrid.ignite.model;

import java.sql.Date;
import java.util.List;

public class Orders {

    int orderNumber;
    String orderType;
    Date orderFulfillmentDate;
    OrderLines[] orderLine;
    List<OrderLines> ol;

    public Orders(int orderNumber, String orderType, Date orderFulfillmentDate, List<OrderLines> ol) {
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.orderFulfillmentDate = orderFulfillmentDate;
        this.orderLine = ol.toArray(new OrderLines[ol.size()]);
    }

    public Orders(int orderNumber, String orderType, Date orderFulfillmentDate, OrderLines[] orderLine) {
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.orderFulfillmentDate = orderFulfillmentDate;
        this.orderLine = orderLine;
    }

    public Orders(int orderNumber, String orderType, Date orderFulfillmentDate) {
        this.orderNumber = orderNumber;
        this.orderType = orderType;
        this.orderFulfillmentDate = orderFulfillmentDate;
    }

    public int getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(int orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public Date getOrderFulfillmentDate() {
        return orderFulfillmentDate;
    }

    public void setOrderFulfillmentDate(Date orderFulfillmentDate) {
        this.orderFulfillmentDate = orderFulfillmentDate;
    }

    public OrderLines[] getOrderLine() {
        return orderLine;
    }

    public void setOrderLine(OrderLines[] orderLine) {
        this.orderLine = orderLine;
    }

    public List<OrderLines> getOl() {
        return ol;
    }

    public void setOl(List<OrderLines> ol) {
        this.ol = ol;
    }

    @Override
    public String toString() {
        return "Orders{" + "orderNumber=" + orderNumber + ", orderType=" + orderType + ", orderFulfillmentDate=" + orderFulfillmentDate + ", orderLine=" + orderLine + '}';
    }
}
