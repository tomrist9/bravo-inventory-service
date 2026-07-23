package com.bravo.inventory.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

public class BulkSaleResponseDto {

    private final String registerId;
    private final List<SaleLineResultDto> lineResults;
    private final BigDecimal totalAmount;

    public BulkSaleResponseDto(String registerId, List<SaleLineResultDto> lineResults, BigDecimal totalAmount) {
        this.registerId = registerId;
        this.lineResults = lineResults;
        this.totalAmount = totalAmount;
    }

    public String getRegisterId() {

        return registerId;
    }

    public List<SaleLineResultDto> getLineResults() {

        return lineResults;
    }

    public BigDecimal getTotalAmount() {

        return totalAmount;
    }
}