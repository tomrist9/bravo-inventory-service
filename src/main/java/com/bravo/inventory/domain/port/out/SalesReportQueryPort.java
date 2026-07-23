package com.bravo.inventory.domain.port.out;

import com.bravo.inventory.domain.model.TopSellingProduct;
import java.util.List;

public interface SalesReportQueryPort {

    List<TopSellingProduct> fetchDailyTopSellingProducts(int limit);
}