package com.flashdrop.orderService.redis;


import lombok.Getter;

@Getter
public class InventoryResult {

    public enum Status {
        SUCCESS,
        INSUFFICIENT_STOCK,
        PRODUCT_NOT_FOUND,
        ERROR
    }

    private final Status status;
    private final long remainingStock;

    // private constructor — use static methods below
    private InventoryResult(Status status, long remainingStock) {
        this.status = status;
        this.remainingStock = remainingStock;
    }

    // Static factory methods — clean way to create results
    public static InventoryResult success(long remaining) {
        return new InventoryResult(Status.SUCCESS, remaining);
    }

    public static InventoryResult insufficientStock() {
        return new InventoryResult(Status.INSUFFICIENT_STOCK, 0);
    }

    public static InventoryResult notFlashProduct() {
        return new InventoryResult(Status.PRODUCT_NOT_FOUND, 0);
    }

    public static InventoryResult error() {
        return new InventoryResult(Status.ERROR, 0);
    }

}
