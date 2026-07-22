package com.bravo.inventory.domain.model;

import java.math.BigDecimal;

public class Product {
    private final Long id;
    private final String sku;
    private final String name;
    private int quantityInStock;
    private final BigDecimal unitPrice;

    public Product(Long id, String sku, String name, int quantityInStock, BigDecimal unitPrice) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.quantityInStock= quantityInStock;
        this.unitPrice = unitPrice;
    }
    public boolean hasEnoughStock(int quantity) {
        return quantityInStock >= quantity;
    }

    public void reduceStock(int quantity){
        if(quantity<=0){
            throw new IllegalArgumentException("Quantity must be greater than 0");
        }

        if(quantity> this.quantityInStock){
            throw new IllegalArgumentException("Quantity must be less than or equal to the current stock");
        }

        this.quantityInStock -= quantity;
    }

    public Long getId() {
        return id;
    }
    public String getSku() {
        return sku;
    }
    public String getName() {
        return name;
    }
    public int getQuantityInStock() {
        return quantityInStock;
    }
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
}