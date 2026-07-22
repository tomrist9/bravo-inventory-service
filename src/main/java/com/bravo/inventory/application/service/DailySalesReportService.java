package com.bravo.inventory.application.service;

import com.bravo.inventory.domain.model.TopSellingProduct;
import com.bravo.inventory.domain.port.in.GenerateDailySalesReportUseCase;
import com.bravo.inventory.domain.port.out.SalesReportQueryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DailySalesReportService implements GenerateDailySalesReportUseCase {

    private static final int DEFAULT_LIMIT = 10;
    private static final int MAX_LIMIT = 100;

    private final SalesReportQueryPort salesReportQueryPort;

    public DailySalesReportService(SalesReportQueryPort salesReportQueryPort) {
        this.salesReportQueryPort = salesReportQueryPort;
    }

    @Override
    @Transactional(readOnly = true)
    public List<TopSellingProduct> getTopSellingProducts(int limit) {
        int effectiveLimit = limit;
        if (effectiveLimit <= 0) {
            effectiveLimit = DEFAULT_LIMIT;
        }
        if (effectiveLimit > MAX_LIMIT) {
            effectiveLimit = MAX_LIMIT;
        }
        return salesReportQueryPort.fetchDailyTopSellingProducts(effectiveLimit);
    }
}