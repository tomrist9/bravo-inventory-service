package com.bravo.inventory.infrastructure.adapter.in.web.controller;

import com.bravo.inventory.domain.model.TopSellingProduct;
import com.bravo.inventory.domain.port.in.GenerateDailySalesReportUseCase;
import com.bravo.inventory.domain.port.in.ProcessBulkSaleUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ReportController.class)
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GenerateDailySalesReportUseCase generateDailySalesReportUseCase;

    @MockBean
    private ProcessBulkSaleUseCase processBulkSaleUseCase;

    @Test
    void getTopSellingProducts_returnsReport() throws Exception {
        when(generateDailySalesReportUseCase.getTopSellingProducts(5))
                .thenReturn(List.of(
                        new TopSellingProduct(
                                1L,
                                "Coca-Cola 0.5L",
                                42L,
                                BigDecimal.valueOf(50.4)
                        )
                ));

        mockMvc.perform(
                        get("/api/v1/reports/top-selling")
                                .param("limit", "5")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].productId").value(1))
                .andExpect(jsonPath("$[0].totalQuantitySold").value(42));
    }

    @Test
    void getTopSellingProducts_usesDefaultLimitWhenNotProvided()
            throws Exception {

        when(generateDailySalesReportUseCase.getTopSellingProducts(10))
                .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/reports/top-selling"))
                .andExpect(status().isOk());
    }
}