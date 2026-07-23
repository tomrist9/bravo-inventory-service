package com.bravo.inventory.infrastructure.adapter.in.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public class BulkSaleRequestDto {

    @NotBlank(message = "registerId is required")
    private String registerId;

    @NotEmpty(message = "lineItems must contain at least one entry")
    @Valid
    private List<SaleLineItemRequestDto> lineItems;

    public BulkSaleRequestDto() {
    }

    public BulkSaleRequestDto(String registerId, List<SaleLineItemRequestDto> lineItems) {
        this.registerId = registerId;
        this.lineItems = lineItems;
    }

    public String getRegisterId() {
        return registerId;
    }

    public void setRegisterId(String registerId) {
        this.registerId = registerId;
    }

    public List<SaleLineItemRequestDto> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<SaleLineItemRequestDto> lineItems) {
        this.lineItems = lineItems;
    }
}