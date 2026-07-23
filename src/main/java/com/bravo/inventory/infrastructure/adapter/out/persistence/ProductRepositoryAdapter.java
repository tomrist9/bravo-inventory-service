package com.bravo.inventory.infrastructure.adapter.out.persistence;

import com.bravo.inventory.domain.model.Product;
import com.bravo.inventory.domain.port.out.ProductRepositoryPort;
import com.bravo.inventory.infrastructure.adapter.out.persistence.entity.ProductJpaEntity;
import com.bravo.inventory.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ProductRepositoryAdapter implements ProductRepositoryPort {

    private final ProductJpaRepository productJpaRepository;

    public ProductRepositoryAdapter(ProductJpaRepository productJpaRepository) {
        this.productJpaRepository = productJpaRepository;
    }

    @Override
    public Optional<Product> findById(Long id) {

        return productJpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<Product> findByIdForUpdate(Long id) {
        return productJpaRepository.findByIdForUpdate(id).map(this::toDomain);
    }

    @Override
    public void save(Product product) {
        ProductJpaEntity entity = productJpaRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException(
                        "Product disappeared mid-transaction, id=" + product.getId()));
        entity.setQuantityInStock(product.getQuantityInStock());
        productJpaRepository.save(entity);
    }

    private Product toDomain(ProductJpaEntity entity) {
        return new Product(
                entity.getId(),
                entity.getSku(),
                entity.getName(),
                entity.getQuantityInStock(),
                entity.getUnitPrice());
    }
}