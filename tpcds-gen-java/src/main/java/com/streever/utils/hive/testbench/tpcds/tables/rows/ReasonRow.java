/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.tables.rows;

/**
 * Row structure for the reason table.
 */
public class ReasonRow {
    public long r_reason_sk;          // Surrogate key
    public String r_reason_id;        // Business key
    public String r_reason_description; // Reason description
}
