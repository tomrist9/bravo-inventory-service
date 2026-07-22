package com.bravo.inventory.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SaleRecord {

    private final Long productId;
    private final String productName;
    private final int quantity;
    private final BigDecimal unitPrice;
    private final LocalDateTime soldAt;

    public SaleRecord(Long productId, String productName, int quantity,
                       BigDecimal unitPrice, LocalDateTime soldAt) {
        this.productId = productId;
        this.productName = productName;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.soldAt = soldAt;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public LocalDateTime getSoldAt() {
        return soldAt;
    }
}