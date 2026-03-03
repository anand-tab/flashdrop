package com.flashdrop.common.events;

public class StockUpdatedEvent extends BaseEvent{

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Integer getRemainingStock() {
        return remainingStock;
    }

    public void setRemainingStock(Integer remainingStock) {
        this.remainingStock = remainingStock;
    }

    private Long productId;
    private Integer remainingStock;


    public StockUpdatedEvent() {}

    public StockUpdatedEvent(Long productId, Integer remainingStock) {
        super();
        this.productId = productId;
        this.remainingStock = remainingStock;
    }
}
