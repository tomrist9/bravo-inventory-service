package com.bravo.inventory.domain.model;

public class SaleLineItem {

    private final Long productId;
    private final int quantity;

    public SaleLineItem(Long productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public Long getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}