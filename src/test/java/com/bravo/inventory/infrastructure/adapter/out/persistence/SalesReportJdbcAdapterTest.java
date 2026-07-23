package com.bravo.inventory.infrastructure.adapter.out.persistence;

import com.bravo.inventory.domain.model.TopSellingProduct;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.test.util.ReflectionTestUtils;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SalesReportJdbcAdapterTest {

    private SalesReportJdbcAdapter adapter;
    private StubSimpleJdbcCall jdbcCall;

    @BeforeEach
    void setUp() {
        DataSource dataSource = mock(DataSource.class);

        adapter = new SalesReportJdbcAdapter(dataSource);
        jdbcCall = new StubSimpleJdbcCall(dataSource);

        ReflectionTestUtils.setField(
                adapter,
                "salesReportCall",
                jdbcCall
        );
    }

    @Test
    void fetchDailyTopSellingProducts_mapsListRows() {
        List<Map<String, Object>> rows = List.of(
                Map.of(
                        "product_id", 1L,
                        "product_name", "Coca-Cola 0.5L",
                        "total_quantity", 10L,
                        "total_revenue", BigDecimal.valueOf(12)
                )
        );

        jdbcCall.response = cursorResult(rows);

        List<TopSellingProduct> result =
                adapter.fetchDailyTopSellingProducts(5);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new TopSellingProduct(
                                1L,
                                "Coca-Cola 0.5L",
                                10L,
                                BigDecimal.valueOf(12)
                        )
                ));

        assertEquals(
                5,
                jdbcCall.parameters.getValue("p_limit")
        );

        assertNull(
                jdbcCall.parameters.getValue("p_cursor")
        );
    }

    @Test
    void fetchDailyTopSellingProducts_mapsResultSet()
            throws SQLException {

        ResultSet resultSet = mock(ResultSet.class);

        when(resultSet.next()).thenReturn(true, false);
        when(resultSet.getLong("product_id")).thenReturn(1L);
        when(resultSet.getString("product_name"))
                .thenReturn("Coca-Cola 0.5L");
        when(resultSet.getLong("total_quantity")).thenReturn(10L);
        when(resultSet.getBigDecimal("total_revenue"))
                .thenReturn(BigDecimal.valueOf(12));

        jdbcCall.response = cursorResult(resultSet);

        List<TopSellingProduct> result =
                adapter.fetchDailyTopSellingProducts(5);

        assertThat(result)
                .usingRecursiveComparison()
                .isEqualTo(List.of(
                        new TopSellingProduct(
                                1L,
                                "Coca-Cola 0.5L",
                                10L,
                                BigDecimal.valueOf(12)
                        )
                ));
    }

    @Test
    void fetchDailyTopSellingProducts_returnsEmptyListForUnknownResult() {
        jdbcCall.response = cursorResult("unsupported-result");

        List<TopSellingProduct> result =
                adapter.fetchDailyTopSellingProducts(5);

        assertTrue(result.isEmpty());
    }

    @Test
    void fetchDailyTopSellingProducts_wrapsProcedureException() {
        RuntimeException cause =
                new RuntimeException("Database error");

        jdbcCall.exception = cause;

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> adapter.fetchDailyTopSellingProducts(5)
        );

        assertEquals(
                "Failed to execute stored procedure get_daily_sales_report",
                exception.getMessage()
        );

        assertSame(cause, exception.getCause());
    }

    @Test
    void fetchDailyTopSellingProducts_wrapsResultSetException()
            throws SQLException {

        ResultSet resultSet = mock(ResultSet.class);
        SQLException cause = new SQLException("Cursor error");

        when(resultSet.next()).thenThrow(cause);

        jdbcCall.response = cursorResult(resultSet);

        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> adapter.fetchDailyTopSellingProducts(5)
        );

        assertEquals(
                "Failed to read sales report cursor",
                exception.getMessage()
        );

        assertSame(cause, exception.getCause());
    }

    private Map<String, Object> cursorResult(Object cursor) {
        Map<String, Object> result = new HashMap<>();
        result.put("p_cursor", cursor);
        return result;
    }

    private static class StubSimpleJdbcCall extends SimpleJdbcCall {

        private Map<String, Object> response = Map.of();
        private RuntimeException exception;
        private SqlParameterSource parameters;

        StubSimpleJdbcCall(DataSource dataSource) {
            super(dataSource);
        }

        @Override
        public Map<String, Object> execute(
                SqlParameterSource parameters
        ) {
            this.parameters = parameters;

            if (exception != null) {
                throw exception;
            }

            return response;
        }
    }
}