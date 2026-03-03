package com.flashdrop.common.events;

public class OrderCreatedEvent extends BaseEvent {

    private Long orderId;
    private Long productId;
    private Long userId;
    private Integer quantity;

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(Long orderId, Long productId, Long userId, Integer quantity) {
        super();
        this.orderId = orderId;
        this.productId = productId;
        this.userId = userId;
        this.quantity = quantity;
    }

    public Long getOrderId() { return orderId; }
    public Long getProductId() { return productId; }
    public Long getUserId() { return userId; }
    public Integer getQuantity() { return quantity; }
}
