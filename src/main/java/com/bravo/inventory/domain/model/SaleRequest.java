package com.bravo.inventory.domain.model;

import java.util.List;

public class SaleRequest {
    private final String registerId;
    private final List<SaleLineItem> lineItems;

    public SaleRequest(String registerId, List<SaleLineItem> lineItems) {
        this.registerId = registerId;
        this.lineItems = lineItems;
    }

    public String getRegisterId() {
        return registerId;
    }

    public List<SaleLineItem> getLineItems() {
        return lineItems;
    }
}