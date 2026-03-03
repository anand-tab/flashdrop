package com.flashdrop.common.events;

import com.flashdrop.common.enums.OrderStatus;

public class OrderResultEvent extends BaseEvent {

    private Long orderId;

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    private OrderStatus status;
    private String message;

    public OrderResultEvent() {}

    public OrderResultEvent(Long orderId, OrderStatus status, String message) {
        super();
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public Long getOrderId() { return orderId; }
    public OrderStatus getStatus() { return status; }
    public String getMessage() { return message; }
}
