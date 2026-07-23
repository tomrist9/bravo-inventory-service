package com.bravo.inventory.application.service;

import com.bravo.inventory.domain.model.TopSellingProduct;
import com.bravo.inventory.domain.port.out.SalesReportQueryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DailySalesReportServiceTest {

    @Mock
    private SalesReportQueryPort salesReportQueryPort;

    private DailySalesReportService dailySalesReportService;

    @BeforeEach
    void setUp() {
        dailySalesReportService = new DailySalesReportService(salesReportQueryPort);
    }

    @Test
    void getTopSellingProducts_delegatesToPortWithGivenLimit() {
        List<TopSellingProduct> expected = List.of(
                new TopSellingProduct(1L, "Coca-Cola 0.5L", 42L, BigDecimal.valueOf(50.4)));
        when(salesReportQueryPort.fetchDailyTopSellingProducts(5)).thenReturn(expected);

        List<TopSellingProduct> result = dailySalesReportService.getTopSellingProducts(5);

        assertThat(result).isEqualTo(expected);
        verify(salesReportQueryPort).fetchDailyTopSellingProducts(5);
    }

    @Test
    void getTopSellingProducts_defaultsToTenWhenLimitNonPositive() {
        dailySalesReportService.getTopSellingProducts(0);
        verify(salesReportQueryPort).fetchDailyTopSellingProducts(10);
    }

    @Test
    void getTopSellingProducts_cappedAtMaximum() {
        dailySalesReportService.getTopSellingProducts(1000);
        verify(salesReportQueryPort).fetchDailyTopSellingProducts(100);
    }
}