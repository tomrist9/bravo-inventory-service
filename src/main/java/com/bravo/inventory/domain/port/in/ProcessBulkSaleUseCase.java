package com.bravo.inventory.domain.port.in;

import com.bravo.inventory.domain.model.BulkSaleResult;
import com.bravo.inventory.domain.model.SaleRequest;

public interface ProcessBulkSaleUseCase {

    BulkSaleResult processBulkSale(SaleRequest saleRequest);
}