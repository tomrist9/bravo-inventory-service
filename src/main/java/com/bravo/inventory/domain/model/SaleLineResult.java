package com.bravo.inventory.domain.model;
import java.math.BigDecimal;

public class SaleLineResult {


        private final Long productId;
        private final String productName;
        private final int quantitySold;
        private final BigDecimal unitPrice;
        private final BigDecimal lineTotal;
        private final int remainingStock;

        public SaleLineResult(Long productId, String productName, int quantitySold,
                              BigDecimal unitPrice, int remainingStock) {
            this.productId = productId;
            this.productName = productName;
            this.quantitySold = quantitySold;
            this.unitPrice = unitPrice;
            this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantitySold));
            this.remainingStock = remainingStock;
        }

        public Long getProductId() {
            return productId;
        }

        public String getProductName() {
            return productName;
        }

        public int getQuantitySold() {
            return quantitySold;
        }

        public BigDecimal getUnitPrice() {
            return unitPrice;
        }

        public BigDecimal getLineTotal() {
            return lineTotal;
        }

        public int getRemainingStock() {
            return remainingStock;
        }
}