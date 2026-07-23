package com.bravo.inventory.application.service;

import com.bravo.inventory.domain.exception.InsufficientStockException;
import com.bravo.inventory.domain.exception.InvalidSaleRequestException;
import com.bravo.inventory.domain.exception.ProductNotFoundException;
import com.bravo.inventory.domain.model.BulkSaleResult;
import com.bravo.inventory.domain.model.Product;
import com.bravo.inventory.domain.model.SaleLineItem;
import com.bravo.inventory.domain.model.SaleRecord;
import com.bravo.inventory.domain.model.SaleRequest;
import com.bravo.inventory.domain.port.out.ProductRepositoryPort;
import com.bravo.inventory.domain.port.out.SaleRecordRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BulkSaleServiceTest {

    @Mock
    private ProductRepositoryPort productRepositoryPort;

    @Mock
    private SaleRecordRepositoryPort saleRecordRepositoryPort;

    private BulkSaleService bulkSaleService;

    @BeforeEach
    void setUp() {
        bulkSaleService = new BulkSaleService(productRepositoryPort, saleRecordRepositoryPort);
    }

    @Test
    void processBulkSale_reducesStockAndReturnsResult() {
        Product cola = new Product(1L, "BRV-0001", "Coca-Cola 0.5L", 10, BigDecimal.valueOf(1.2));
        when(productRepositoryPort.findByIdForUpdate(1L)).thenReturn(Optional.of(cola));

        SaleRequest request = new SaleRequest("REG-1", List.of(new SaleLineItem(1L, 3)));

        BulkSaleResult result = bulkSaleService.processBulkSale(request);

        assertThat(result.getLineResults()).hasSize(1);
        assertThat(result.getLineResults().get(0).getRemainingStock()).isEqualTo(7);
        assertThat(result.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(3.6));

        verify(productRepositoryPort).save(any(Product.class));

        ArgumentCaptor<List<SaleRecord>> captor = ArgumentCaptor.forClass(List.class);
        verify(saleRecordRepositoryPort).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getQuantity()).isEqualTo(3);
    }

    @Test
    void processBulkSale_processesMultipleLineItemsInProductIdOrder() {
        Product productA = new Product(5L, "SKU-5", "Product A", 10, BigDecimal.valueOf(2));
        Product productB = new Product(2L, "SKU-2", "Product B", 10, BigDecimal.valueOf(3));
        when(productRepositoryPort.findByIdForUpdate(5L)).thenReturn(Optional.of(productA));
        when(productRepositoryPort.findByIdForUpdate(2L)).thenReturn(Optional.of(productB));

        SaleRequest request = new SaleRequest("REG-1",
                List.of(new SaleLineItem(5L, 1), new SaleLineItem(2L, 2)));

        BulkSaleResult result = bulkSaleService.processBulkSale(request);

        assertThat(result.getLineResults()).hasSize(2);
        // Product with the lower id (2) should be processed first.
        assertThat(result.getLineResults().get(0).getProductId()).isEqualTo(2L);
        assertThat(result.getLineResults().get(1).getProductId()).isEqualTo(5L);
    }

    @Test
    void processBulkSale_throwsWhenProductNotFound() {
        when(productRepositoryPort.findByIdForUpdate(99L)).thenReturn(Optional.empty());

        SaleRequest request = new SaleRequest("REG-1", List.of(new SaleLineItem(99L, 1)));

        assertThatThrownBy(() -> bulkSaleService.processBulkSale(request))
                .isInstanceOf(ProductNotFoundException.class);

        verify(saleRecordRepositoryPort, never()).saveAll(any());
    }

    @Test
    void processBulkSale_throwsWhenInsufficientStock() {
        Product product = new Product(1L, "SKU-1", "Widget", 2, BigDecimal.valueOf(5));
        when(productRepositoryPort.findByIdForUpdate(1L)).thenReturn(Optional.of(product));

        SaleRequest request = new SaleRequest("REG-1", List.of(new SaleLineItem(1L, 5)));

        assertThatThrownBy(() -> bulkSaleService.processBulkSale(request))
                .isInstanceOf(InsufficientStockException.class);

        verify(productRepositoryPort, never()).save(any());
    }

    @Test
    void processBulkSale_rejectsEmptyLineItems() {
        SaleRequest request = new SaleRequest("REG-1", List.of());

        assertThatThrownBy(() -> bulkSaleService.processBulkSale(request))
                .isInstanceOf(InvalidSaleRequestException.class);
    }

    @Test
    void processBulkSale_rejectsNullRequest() {
        assertThatThrownBy(() -> bulkSaleService.processBulkSale(null))
                .isInstanceOf(InvalidSaleRequestException.class);
    }

    @Test
    void processBulkSale_rejectsNonPositiveQuantity() {
        SaleRequest request = new SaleRequest("REG-1", List.of(new SaleLineItem(1L, 0)));

        assertThatThrownBy(() -> bulkSaleService.processBulkSale(request))
                .isInstanceOf(InvalidSaleRequestException.class);

        verify(productRepositoryPort, never()).findByIdForUpdate(any());
    }
}