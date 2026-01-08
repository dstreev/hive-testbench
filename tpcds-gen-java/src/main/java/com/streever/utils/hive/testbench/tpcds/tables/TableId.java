/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.tables;

/**
 * Table identifiers matching the TPC-DS specification.
 * These values match tables.h in the C implementation.
 */
public enum TableId {
    // Warehouse tables (dimension and fact)
    CALL_CENTER(0, "call_center", "cc"),
    CATALOG_PAGE(1, "catalog_page", "cp"),
    CATALOG_RETURNS(2, "catalog_returns", "cr"),
    CATALOG_SALES(3, "catalog_sales", "cs"),
    CUSTOMER(4, "customer", "c"),
    CUSTOMER_ADDRESS(5, "customer_address", "ca"),
    CUSTOMER_DEMOGRAPHICS(6, "customer_demographics", "cd"),
    DATE_DIM(7, "date_dim", "d"),
    HOUSEHOLD_DEMOGRAPHICS(8, "household_demographics", "hd"),
    INCOME_BAND(9, "income_band", "ib"),
    INVENTORY(10, "inventory", "inv"),
    ITEM(11, "item", "i"),
    PROMOTION(12, "promotion", "p"),
    REASON(13, "reason", "r"),
    SHIP_MODE(14, "ship_mode", "sm"),
    STORE(15, "store", "s"),
    STORE_RETURNS(16, "store_returns", "sr"),
    STORE_SALES(17, "store_sales", "ss"),
    TIME_DIM(18, "time_dim", "t"),
    WAREHOUSE(19, "warehouse", "w"),
    WEB_PAGE(20, "web_page", "wp"),
    WEB_RETURNS(21, "web_returns", "wr"),
    WEB_SALES(22, "web_sales", "ws"),
    WEB_SITE(23, "web_site", "web");

    private final int id;
    private final String name;
    private final String abbreviation;

    TableId(int id, String name, String abbreviation) {
        this.id = id;
        this.name = name;
        this.abbreviation = abbreviation;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public static TableId fromId(int id) {
        for (TableId t : values()) {
            if (t.id == id) {
                return t;
            }
        }
        return null;
    }

    public static TableId fromName(String name) {
        for (TableId t : values()) {
            if (t.name.equalsIgnoreCase(name) || t.abbreviation.equalsIgnoreCase(name)) {
                return t;
            }
        }
        return null;
    }
}
