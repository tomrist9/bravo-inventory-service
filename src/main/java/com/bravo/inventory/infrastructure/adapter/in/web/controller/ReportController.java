package com.bravo.inventory.infrastructure.adapter.in.web.controller;

import com.bravo.inventory.domain.model.TopSellingProduct;
import com.bravo.inventory.domain.port.in.GenerateDailySalesReportUseCase;
import com.bravo.inventory.infrastructure.adapter.in.web.dto.TopSellingProductDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/reports")
public class ReportController {

    private final GenerateDailySalesReportUseCase generateDailySalesReportUseCase;

    public ReportController(GenerateDailySalesReportUseCase generateDailySalesReportUseCase) {
        this.generateDailySalesReportUseCase = generateDailySalesReportUseCase;
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<TopSellingProductDto>> getTopSellingProducts(
            @RequestParam(name = "limit", defaultValue = "10") int limit) {

        List<TopSellingProduct> products = generateDailySalesReportUseCase.getTopSellingProducts(limit);

        List<TopSellingProductDto> dtos = products.stream()
                .map(p -> new TopSellingProductDto(
                        p.getProductId(), p.getProductName(), p.getTotalQuantitySold(), p.getTotalRevenue()))
                .toList();

        return ResponseEntity.ok(dtos);
    }
}