package com.bravo.inventory.infrastructure.adapter.in.web.controller;

import com.bravo.inventory.domain.exception.InsufficientStockException;
import com.bravo.inventory.domain.exception.ProductNotFoundException;
import com.bravo.inventory.domain.model.BulkSaleResult;
import com.bravo.inventory.domain.model.SaleLineResult;
import com.bravo.inventory.domain.port.in.GenerateDailySalesReportUseCase;
import com.bravo.inventory.domain.port.in.ProcessBulkSaleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SaleController.class)
class SaleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProcessBulkSaleUseCase processBulkSaleUseCase;

    @MockBean
    private GenerateDailySalesReportUseCase generateDailySalesReportUseCase;

    @Test
    void submitBulkSale_returns201WithResult() throws Exception {
        SaleLineResult lineResult = new SaleLineResult(
                1L,
                "Coca-Cola 0.5L",
                3,
                BigDecimal.valueOf(1.2),
                7
        );

        BulkSaleResult result = new BulkSaleResult(
                "REG-1",
                List.of(lineResult)
        );

        when(processBulkSaleUseCase.processBulkSale(any()))
                .thenReturn(result);

        String requestBody = """
                {
                  "registerId": "REG-1",
                  "lineItems": [
                    {
                      "productId": 1,
                      "quantity": 3
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sales/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.registerId").value("REG-1"))
                .andExpect(jsonPath("$.lineResults[0].productId").value(1))
                .andExpect(jsonPath("$.lineResults[0].remainingStock").value(7));
    }

    @Test
    void submitBulkSale_returns400WhenLineItemsMissing() throws Exception {
        String requestBody = """
                {
                  "registerId": "REG-1",
                  "lineItems": []
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sales/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void submitBulkSale_returns404WhenProductNotFound() throws Exception {
        when(processBulkSaleUseCase.processBulkSale(any()))
                .thenThrow(new ProductNotFoundException(99L));

        String requestBody = """
                {
                  "registerId": "REG-1",
                  "lineItems": [
                    {
                      "productId": 99,
                      "quantity": 1
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sales/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404));
    }

    @Test
    void submitBulkSale_returns409WhenInsufficientStock() throws Exception {
        when(processBulkSaleUseCase.processBulkSale(any()))
                .thenThrow(new InsufficientStockException(1L, 5, 2));

        String requestBody = """
                {
                  "registerId": "REG-1",
                  "lineItems": [
                    {
                      "productId": 1,
                      "quantity": 5
                    }
                  ]
                }
                """;

        mockMvc.perform(
                        post("/api/v1/sales/bulk")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }
}