/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds.util;

import org.tpcds.tables.TableId;

/**
 * Scaling functions for TPC-DS tables.
 * Row counts depend on the scale factor.
 */
public class Scaling {

    // Row counts at scale factor 1 (1GB)
    private static final long CALL_CENTER_BASE = 6;
    private static final long CATALOG_PAGE_BASE = 11718;
    private static final long CUSTOMER_BASE = 100000;
    private static final long CUSTOMER_ADDRESS_BASE = 50000;
    private static final long CUSTOMER_DEMOGRAPHICS_BASE = 1920800;
    private static final long DATE_DIM_BASE = 73049;
    private static final long HOUSEHOLD_DEMOGRAPHICS_BASE = 7200;
    private static final long INCOME_BAND_BASE = 20;
    private static final long INVENTORY_BASE = 11745000;
    private static final long ITEM_BASE = 18000;
    private static final long PROMOTION_BASE = 300;
    private static final long REASON_BASE = 35;
    private static final long SHIP_MODE_BASE = 20;
    private static final long STORE_BASE = 12;
    private static final long STORE_RETURNS_BASE = 287514;
    private static final long STORE_SALES_BASE = 2880404;
    private static final long TIME_DIM_BASE = 86400;
    private static final long WAREHOUSE_BASE = 5;
    private static final long WEB_PAGE_BASE = 60;
    private static final long WEB_RETURNS_BASE = 71763;
    private static final long WEB_SALES_BASE = 719384;
    private static final long WEB_SITE_BASE = 30;
    private static final long CATALOG_RETURNS_BASE = 144067;
    private static final long CATALOG_SALES_BASE = 1441548;

    /**
     * Get row count for a table at a given scale factor.
     */
    public static long getRowCount(TableId tableId, int scaleFactor) {
        return getRowCount(tableId.getName(), scaleFactor);
    }

    /**
     * Get row count for a table at a given scale factor.
     */
    public static long getRowCount(String tableName, int scaleFactor) {
        switch (tableName.toLowerCase()) {
            case "call_center":
                return Math.max(CALL_CENTER_BASE, CALL_CENTER_BASE * scaleFactor / 10);
            case "catalog_page":
                return CATALOG_PAGE_BASE + (scaleFactor - 1) * 1000;
            case "catalog_returns":
                return CATALOG_RETURNS_BASE * scaleFactor;
            case "catalog_sales":
                return CATALOG_SALES_BASE * scaleFactor;
            case "customer":
                return CUSTOMER_BASE * scaleFactor;
            case "customer_address":
                return CUSTOMER_ADDRESS_BASE * scaleFactor;
            case "customer_demographics":
                return CUSTOMER_DEMOGRAPHICS_BASE;  // Fixed
            case "date_dim":
                return DATE_DIM_BASE;  // Fixed
            case "household_demographics":
                return HOUSEHOLD_DEMOGRAPHICS_BASE;  // Fixed
            case "income_band":
                return INCOME_BAND_BASE;  // Fixed
            case "inventory":
                return INVENTORY_BASE * scaleFactor / 100;  // Approximate
            case "item":
                return Math.max(ITEM_BASE, ITEM_BASE * scaleFactor / 10);
            case "promotion":
                return PROMOTION_BASE * scaleFactor;
            case "reason":
                return REASON_BASE;  // Fixed
            case "ship_mode":
                return SHIP_MODE_BASE;  // Fixed
            case "store":
                return Math.max(STORE_BASE, STORE_BASE * scaleFactor / 10);
            case "store_returns":
                return STORE_RETURNS_BASE * scaleFactor;
            case "store_sales":
                return STORE_SALES_BASE * scaleFactor;
            case "time_dim":
                return TIME_DIM_BASE;  // Fixed
            case "warehouse":
                return Math.max(WAREHOUSE_BASE, WAREHOUSE_BASE * scaleFactor / 10);
            case "web_page":
                return WEB_PAGE_BASE * scaleFactor;
            case "web_returns":
                return WEB_RETURNS_BASE * scaleFactor;
            case "web_sales":
                return WEB_SALES_BASE * scaleFactor;
            case "web_site":
                return Math.max(WEB_SITE_BASE, WEB_SITE_BASE * scaleFactor / 10);
            default:
                throw new IllegalArgumentException("Unknown table: " + tableName);
        }
    }
}
