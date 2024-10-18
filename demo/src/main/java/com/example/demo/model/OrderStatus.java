package com.example.demo.model;

public enum OrderStatus {
    CANCELED("已取消"),
    PENDING_PAYMENT("待付款"),
    PAID("已付款"),
    SHIPPED("已发货"),
    AWAITING_RECEIPT("待收货"),
    RECEIVED("已收货");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
