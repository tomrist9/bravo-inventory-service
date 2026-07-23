package com.bravo.inventory.infrastructure.adapter.out.persistence;

import com.bravo.inventory.domain.model.TopSellingProduct;
import com.bravo.inventory.domain.port.out.SalesReportQueryPort;
import org.springframework.jdbc.core.SqlInOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Component
public class SalesReportJdbcAdapter implements SalesReportQueryPort {

    private static final String PROCEDURE_NAME = "get_daily_sales_report";
    private static final String CURSOR_PARAM = "p_cursor";
    private static final String LIMIT_PARAM = "p_limit";

    private final SimpleJdbcCall salesReportCall;


    public SalesReportJdbcAdapter(DataSource dataSource) {
        this.salesReportCall = new SimpleJdbcCall(dataSource)
                .withProcedureName(PROCEDURE_NAME)
                .withoutProcedureColumnMetaDataAccess()
                .declareParameters(
                        new SqlParameter(LIMIT_PARAM, Types.INTEGER),
                        new SqlInOutParameter(CURSOR_PARAM, Types.OTHER));
    }

    @Override
    @Transactional
    public List<TopSellingProduct> fetchDailyTopSellingProducts(int limit) {
        Map<String, Object> result;
        try {
            result = salesReportCall.execute(
                    new MapSqlParameterSource()
                            .addValue(LIMIT_PARAM, limit, Types.INTEGER)
                            .addValue(CURSOR_PARAM, null, Types.OTHER));
        } catch (Exception ex) {
            throw new IllegalStateException(
                    "Failed to execute stored procedure " + PROCEDURE_NAME, ex);
        }

        Object cursorResult = result.get(CURSOR_PARAM);
        if (cursorResult instanceof ResultSet resultSet) {
            return mapResultSet(resultSet);
        }

        if (cursorResult instanceof List<?> rows) {
            return mapRows(rows);
        }
        return List.of();
    }

    private List<TopSellingProduct> mapResultSet(ResultSet rs) {
        List<TopSellingProduct> products = new ArrayList<>();
        try {
            while (rs.next()) {
                products.add(new TopSellingProduct(
                        rs.getLong("product_id"),
                        rs.getString("product_name"),
                        rs.getLong("total_quantity"),
                        rs.getBigDecimal("total_revenue")));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Failed to read sales report cursor", e);
        }
        return products;
    }

    @SuppressWarnings("unchecked")
    private List<TopSellingProduct> mapRows(List<?> rows) {
        List<TopSellingProduct> products = new ArrayList<>();
        for (Object row : rows) {
            Map<String, Object> m = (Map<String, Object>) row;
            products.add(new TopSellingProduct(
                    ((Number) m.get("product_id")).longValue(),
                    (String) m.get("product_name"),
                    ((Number) m.get("total_quantity")).longValue(),
                    (BigDecimal) m.get("total_revenue")));
        }
        return products;
    }

}