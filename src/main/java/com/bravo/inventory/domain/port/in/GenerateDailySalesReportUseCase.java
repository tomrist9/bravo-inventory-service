package com.bravo.inventory.domain.port.in;

import com.bravo.inventory.domain.model.TopSellingProduct;

import java.util.List;

public interface GenerateDailySalesReportUseCase {

    List<TopSellingProduct> getTopSellingProducts(int limit);
}