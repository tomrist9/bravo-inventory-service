package com.bravo.inventory.domain.port.out;

import com.bravo.inventory.domain.model.SaleRecord;

import java.util.List;

public interface SaleRecordRepositoryPort {

    void saveAll(List<SaleRecord> saleRecords);
}