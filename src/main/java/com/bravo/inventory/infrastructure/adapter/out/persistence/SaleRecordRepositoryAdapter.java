package com.bravo.inventory.infrastructure.adapter.out.persistence;

import com.bravo.inventory.domain.model.SaleRecord;
import com.bravo.inventory.domain.port.out.SaleRecordRepositoryPort;
import com.bravo.inventory.infrastructure.adapter.out.persistence.entity.SaleRecordJpaEntity;
import com.bravo.inventory.infrastructure.adapter.out.persistence.repository.SaleRecordJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleRecordRepositoryAdapter implements SaleRecordRepositoryPort {

    private final SaleRecordJpaRepository saleRecordJpaRepository;

    public SaleRecordRepositoryAdapter(SaleRecordJpaRepository saleRecordJpaRepository) {
        this.saleRecordJpaRepository = saleRecordJpaRepository;
    }

    @Override
    public void saveAll(List<SaleRecord> saleRecords) {
        List<SaleRecordJpaEntity> entities = saleRecords.stream()
                .map(r -> new SaleRecordJpaEntity(
                        r.getProductId(), r.getProductName(), r.getQuantity(),
                        r.getUnitPrice(), r.getSoldAt()))
                .toList();
        saleRecordJpaRepository.saveAll(entities);
    }
}