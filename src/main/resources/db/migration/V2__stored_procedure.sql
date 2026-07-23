CREATE OR REPLACE PROCEDURE get_daily_sales_report(
    IN  p_limit  INTEGER,
    INOUT p_cursor REFCURSOR
)
LANGUAGE plpgsql
AS $$
BEGIN
OPEN p_cursor FOR
SELECT
    product_id,
    product_name,
    SUM(quantity)::BIGINT               AS total_quantity,
    SUM(quantity * unit_price)::NUMERIC  AS total_revenue
FROM sale_records
WHERE sale_date = CURRENT_DATE
GROUP BY product_id, product_name
ORDER BY total_quantity DESC
    LIMIT p_limit;
END;
$$;
