/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds.tables.rows;

/**
 * Row structure for the date_dim table.
 */
public class DateDimRow {
    public long d_date_sk;          // Surrogate key
    public String d_date_id;        // Business key
    public String d_date;           // Date string (YYYY-MM-DD)
    public int d_month_seq;         // Sequential month number
    public int d_week_seq;          // Sequential week number
    public int d_quarter_seq;       // Sequential quarter number
    public int d_year;              // Year
    public int d_dow;               // Day of week (0=Sunday)
    public int d_moy;               // Month of year
    public int d_dom;               // Day of month
    public int d_qoy;               // Quarter of year
    public int d_fy_year;           // Fiscal year
    public int d_fy_quarter_seq;    // Fiscal quarter sequence
    public int d_fy_week_seq;       // Fiscal week sequence
    public String d_day_name;       // Day name (Sunday, Monday, etc.)
    public String d_quarter_name;   // Quarter name (e.g., "2001Q1")
    public int d_holiday;           // Is holiday (0 or 1)
    public int d_weekend;           // Is weekend (0 or 1)
    public int d_following_holiday; // Following day is holiday (0 or 1)
    public int d_first_dom;         // First day of month (Julian)
    public int d_last_dom;          // Last day of month (Julian)
    public int d_same_day_ly;       // Same day last year (Julian)
    public int d_same_day_lq;       // Same day last quarter (Julian)
    public int d_current_day;       // Is current day (0 or 1)
    public int d_current_week;      // Is current week (0 or 1)
    public int d_current_month;     // Is current month (0 or 1)
    public int d_current_quarter;   // Is current quarter (0 or 1)
    public int d_current_year;      // Is current year (0 or 1)
}
