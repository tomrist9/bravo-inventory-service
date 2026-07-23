package com.bravo.inventory.domain.model;


import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductTest {

    @Test
    void hasEnoughStock_returnsTrueWhenStockCoversRequest() {
        Product product = new Product(1L, "SKU-1", "Widget", 10, BigDecimal.valueOf(5));

        assertThat(product.hasEnoughStock(10)).isTrue();
        assertThat(product.hasEnoughStock(11)).isFalse();
    }

    @Test
    void reduceStock_decrementsQuantity() {
        Product product = new Product(1L, "SKU-1", "Widget", 10, BigDecimal.valueOf(5));

        product.reduceStock(4);

        assertThat(product.getQuantityInStock()).isEqualTo(6);
    }

    @Test
    void reduceStock_rejectsNonPositiveQuantity() {
        Product product = new Product(1L, "SKU-1", "Widget", 10, BigDecimal.valueOf(5));

        assertThatThrownBy(() -> product.reduceStock(0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> product.reduceStock(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void reduceStock_rejectsReductionBelowZero() {
        Product product = new Product(1L, "SKU-1", "Widget", 3, BigDecimal.valueOf(5));

        assertThatThrownBy(() -> product.reduceStock(11))
                .isInstanceOf(IllegalArgumentException.class);
    }
}