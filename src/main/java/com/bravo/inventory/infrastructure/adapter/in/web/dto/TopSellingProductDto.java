package com.bravo.inventory.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public class TopSellingProductDto {

    private final Long productId;
    private final String productName;
    private final long totalQuantitySold;
    private final BigDecimal totalRevenue;

    public TopSellingProductDto(Long productId, String productName, long totalQuantitySold, BigDecimal totalRevenue) {
        this.productId = productId;
        this.productName = productName;
        this.totalQuantitySold = totalQuantitySold;
        this.totalRevenue = totalRevenue;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public long getTotalQuantitySold() {
        return totalQuantitySold;
    }

    public BigDecimal getTotalRevenue() {
        return totalRevenue;
    }
}