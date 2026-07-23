package com.bravo.inventory.infrastructure.adapter.out.persistence.repository;

import com.bravo.inventory.infrastructure.adapter.out.persistence.entity.SaleRecordJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SaleRecordJpaRepository extends JpaRepository<SaleRecordJpaEntity, Long> {
}