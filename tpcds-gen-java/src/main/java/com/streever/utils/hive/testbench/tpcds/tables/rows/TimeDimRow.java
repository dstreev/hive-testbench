/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.tables.rows;

/**
 * Row structure for the time_dim table.
 */
public class TimeDimRow {
    public long t_time_sk;      // Surrogate key
    public String t_time_id;    // Business key
    public int t_time;          // Seconds since midnight
    public int t_hour;          // Hour (0-23)
    public int t_minute;        // Minute (0-59)
    public int t_second;        // Second (0-59)
    public String t_am_pm;      // AM or PM
    public String t_shift;      // Shift name
    public String t_sub_shift;  // Sub-shift name
    public String t_meal_time;  // Meal time
}
