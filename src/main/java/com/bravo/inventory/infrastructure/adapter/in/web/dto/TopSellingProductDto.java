package com.bravo.inventory.infrastructure.adapter.in.web.dto;

import java.math.BigDecimal;

public record TopSellingProductDto(
        Long productId,
        String productName,
        long totalQuantitySold,
        BigDecimal totalRevenue
) {}