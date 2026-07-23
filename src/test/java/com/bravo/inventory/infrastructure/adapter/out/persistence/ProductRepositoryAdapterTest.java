package com.bravo.inventory.infrastructure.adapter.out.persistence;

import com.bravo.inventory.domain.model.Product;
import com.bravo.inventory.infrastructure.adapter.out.persistence.entity.ProductJpaEntity;
import com.bravo.inventory.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductRepositoryAdapterTest {

    @Mock
    private ProductJpaRepository productJpaRepository;

    private ProductRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new ProductRepositoryAdapter(productJpaRepository);
    }

    @Test
    void findByIdForUpdate_mapsEntityToDomain() {
        ProductJpaEntity entity = new ProductJpaEntity(1L, "SKU-1", "Widget", 10, BigDecimal.TEN);
        when(productJpaRepository.findByIdForUpdate(1L)).thenReturn(Optional.of(entity));

        Optional<Product> result = adapter.findByIdForUpdate(1L);

        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("Widget");
        assertThat(result.get().getQuantityInStock()).isEqualTo(10);
    }

    @Test
    void findByIdForUpdate_returnsEmptyWhenMissing() {
        when(productJpaRepository.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        assertThat(adapter.findByIdForUpdate(99L)).isEmpty();
    }

    @Test
    void save_updatesQuantityOnExistingEntity() {
        ProductJpaEntity entity = new ProductJpaEntity(1L, "SKU-1", "Widget", 10, BigDecimal.TEN);
        when(productJpaRepository.findById(1L)).thenReturn(Optional.of(entity));

        Product product = new Product(1L, "SKU-1", "Widget", 4, BigDecimal.TEN);
        adapter.save(product);

        assertThat(entity.getQuantityInStock()).isEqualTo(4);
        verify(productJpaRepository).save(entity);
    }
}