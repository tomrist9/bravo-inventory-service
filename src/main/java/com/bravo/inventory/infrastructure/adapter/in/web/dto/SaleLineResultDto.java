package com.bravo.inventory.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public class SaleLineResultDto {

    private final Long productId;
    private final String productName;
    private final int quantitySold;
    private final BigDecimal unitPrice;
    private final BigDecimal lineTotal;
    private final int remainingStock;

    public SaleLineResultDto(Long productId, String productName, int quantitySold,
                              BigDecimal unitPrice, BigDecimal lineTotal, int remainingStock) {
        this.productId = productId;
        this.productName = productName;
        this.quantitySold = quantitySold;
        this.unitPrice = unitPrice;
        this.lineTotal = lineTotal;
        this.remainingStock = remainingStock;
    }

    public Long getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getLineTotal() {
        return lineTotal;
    }

    public int getRemainingStock() {
        return remainingStock;
    }
}