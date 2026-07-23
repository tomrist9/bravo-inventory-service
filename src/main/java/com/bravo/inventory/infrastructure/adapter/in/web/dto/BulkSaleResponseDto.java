package com.bravo.inventory.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.List;


public record BulkSaleResponseDto(
        String registerId,
        List<SaleLineResultDto> lineResults,
        BigDecimal totalAmount
) {}