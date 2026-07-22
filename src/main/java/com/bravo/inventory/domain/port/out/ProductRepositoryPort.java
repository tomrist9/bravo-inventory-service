package com.bravo.inventory.domain.port.out;

import com.bravo.inventory.domain.model.Product;

import java.util.Optional;

public interface ProductRepositoryPort {

    Optional<Product> findById(Long id);

    Optional<Product> findByIdForUpdate(Long id);

    void save(Product product);
}