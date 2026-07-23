package com.bravo.inventory.application.service;

import com.bravo.inventory.domain.exception.InsufficientStockException;
import com.bravo.inventory.domain.exception.InvalidSaleRequestException;
import com.bravo.inventory.domain.exception.ProductNotFoundException;
import com.bravo.inventory.domain.model.*;
import com.bravo.inventory.domain.port.in.ProcessBulkSaleUseCase;
import com.bravo.inventory.domain.port.out.ProductRepositoryPort;
import com.bravo.inventory.domain.port.out.SaleRecordRepositoryPort;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BulkSaleService implements ProcessBulkSaleUseCase {

    private final ProductRepositoryPort productRepositoryPort;
    private final SaleRecordRepositoryPort saleRecordRepositoryPort;
    public BulkSaleService(ProductRepositoryPort productRepositoryPort,
                           SaleRecordRepositoryPort saleRecordRepositoryPort) {
        this.productRepositoryPort = productRepositoryPort;
        this.saleRecordRepositoryPort = saleRecordRepositoryPort;
    }
    @Override
    @Transactional
    public BulkSaleResult processBulkSale(SaleRequest saleRequest) {
        if(saleRequest == null || saleRequest.getLineItems()==null
        ||saleRequest.getLineItems().isEmpty()){
            throw new InvalidSaleRequestException("Sale request must contain at least one line item!");
        }

        List<SaleLineItem> orderedLineItems = new ArrayList<>(saleRequest.getLineItems());
        orderedLineItems.sort((a,b)->a.getProductId().compareTo(b.getProductId()));

        List<SaleLineResult> lineResults = new ArrayList<>();
        List<SaleRecord> saleRecords = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for(SaleLineItem lineItem : orderedLineItems){
            if(lineItem.getQuantity()<=0){
                throw new InvalidSaleRequestException("Quantity must be greater than 0 for prouctId" + lineItem.getProductId());
            }

            Product product = productRepositoryPort.findByIdForUpdate(lineItem.getProductId())
                    .orElseThrow(()->  new ProductNotFoundException(lineItem.getProductId()));

            if(!product.hasEnoughStock(lineItem.getQuantity())){
                throw new InsufficientStockException(
                        product.getId(), lineItem.getQuantity(), product.getQuantityInStock()
                );
            }
            product.reduceStock(lineItem.getQuantity());
            productRepositoryPort.save(product);

            lineResults.add(new SaleLineResult(
                    product.getId(),
                    product.getName(),
                    lineItem.getQuantity(),
                    product.getUnitPrice(),
                    product.getQuantityInStock()
            ));

            saleRecords.add(new SaleRecord(
                    product.getId(),
                    product.getName(),
                    lineItem.getQuantity(),
                    product.getUnitPrice(),
                    now
            ));
        }

        saleRecordRepositoryPort.saveAll(saleRecords);

        return new BulkSaleResult(saleRequest.getRegisterId(), lineResults);
    }
}