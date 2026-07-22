package com.bravo.inventory.domain.model;


import java.math.BigDecimal;
import java.util.List;

public class BulkSaleResult {

    private final String registerId;
    private final List<SaleLineResult> lineResults;
    private final BigDecimal totalAmount;

    public BulkSaleResult(String registerId, List<SaleLineResult> lineResults) {
        this.registerId = registerId;
        this.lineResults = lineResults;
        this.totalAmount = lineResults.stream()
                .map(SaleLineResult::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getRegisterId() {
        return registerId;
    }

    public List<SaleLineResult> getLineResults() {
        return lineResults;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}