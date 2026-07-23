package com.bravo.inventory.integration;


import com.bravo.inventory.BravoInventoryServiceApplication;
import com.bravo.inventory.domain.exception.InsufficientStockException;
import com.bravo.inventory.domain.model.SaleLineItem;
import com.bravo.inventory.domain.model.SaleRequest;
import com.bravo.inventory.domain.port.in.ProcessBulkSaleUseCase;
import com.bravo.inventory.infrastructure.adapter.out.persistence.repository.ProductJpaRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;


@Testcontainers
@SpringBootTest(classes = BravoInventoryServiceApplication.class)
class ConcurrentBulkSaleIntegrationTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("bravo_inventory_test")
            .withUsername("bravo")
            .withPassword("bravo");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    private ProcessBulkSaleUseCase processBulkSaleUseCase;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    private static final Long POPULAR_PRODUCT_ID = 1L;
    private static final int STARTING_STOCK = 500;
    private static final int CONCURRENT_REGISTERS = 10;
    private static final int UNITS_PER_SALE = 20;

    @BeforeEach
    void resetStock() {
        productJpaRepository.findById(POPULAR_PRODUCT_ID).ifPresent(p -> {
            p.setQuantityInStock(STARTING_STOCK);
            productJpaRepository.save(p);
        });
    }

    @AfterEach
    void tearDown() {

    }

    @Test
    void tenRegistersSellingSameProductSimultaneously_neverGoesNegativeOrLosesUpdates() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(CONCURRENT_REGISTERS);
        CountDownLatch readyLatch = new CountDownLatch(CONCURRENT_REGISTERS);
        CountDownLatch startLatch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failureCount = new AtomicInteger();

        for (int i = 0; i < CONCURRENT_REGISTERS; i++) {
            final String registerId = "REG-" + i;
            executor.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    SaleRequest request = new SaleRequest(registerId,
                            List.of(new SaleLineItem(POPULAR_PRODUCT_ID, UNITS_PER_SALE)));
                    processBulkSaleUseCase.processBulkSale(request);
                    successCount.incrementAndGet();
                } catch (InsufficientStockException ex) {
                    failureCount.incrementAndGet();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });
        }

        readyLatch.await();
        startLatch.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        int expectedRemainingStock = STARTING_STOCK - (successCount.get() * UNITS_PER_SALE);
        int actualRemainingStock = productJpaRepository.findById(POPULAR_PRODUCT_ID)
                .orElseThrow()
                .getQuantityInStock();

        assertThat(successCount.get() + failureCount.get()).isEqualTo(CONCURRENT_REGISTERS);
        assertThat(actualRemainingStock).isEqualTo(expectedRemainingStock);
        assertThat(actualRemainingStock).isGreaterThanOrEqualTo(0);
    }
}