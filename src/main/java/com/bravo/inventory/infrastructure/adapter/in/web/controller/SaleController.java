package com.bravo.inventory.infrastructure.adapter.in.web.controller;

import com.bravo.inventory.domain.model.BulkSaleResult;
import com.bravo.inventory.domain.model.SaleLineItem;
import com.bravo.inventory.domain.model.SaleRequest;
import com.bravo.inventory.domain.port.in.ProcessBulkSaleUseCase;
import com.bravo.inventory.infrastructure.adapter.in.web.dto.BulkSaleRequestDto;
import com.bravo.inventory.infrastructure.adapter.in.web.dto.BulkSaleResponseDto;
import com.bravo.inventory.infrastructure.adapter.in.web.dto.SaleLineResultDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sales")
public class SaleController {

    private final ProcessBulkSaleUseCase processBulkSaleUseCase;

    public SaleController(ProcessBulkSaleUseCase processBulkSaleUseCase) {
        this.processBulkSaleUseCase = processBulkSaleUseCase;
    }

    @PostMapping("/bulk")
    public ResponseEntity<BulkSaleResponseDto> submitBulkSale(@Valid @RequestBody BulkSaleRequestDto request) {
        SaleRequest domainRequest = new SaleRequest(
                request.getRegisterId(),
                request.getLineItems().stream()
                        .map(li -> new SaleLineItem(li.getProductId(), li.getQuantity()))
                        .toList());

        BulkSaleResult result = processBulkSaleUseCase.processBulkSale(domainRequest);

        List<SaleLineResultDto> lineResultDtos = result.getLineResults().stream()
                .map(r -> new SaleLineResultDto(
                        r.getProductId(), r.getProductName(), r.getQuantitySold(),
                        r.getUnitPrice(), r.getLineTotal(), r.getRemainingStock()))
                .toList();

        BulkSaleResponseDto responseDto = new BulkSaleResponseDto(
                result.getRegisterId(), lineResultDtos, result.getTotalAmount());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }
}