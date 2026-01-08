/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.util;

/**
 * Address data structure used by multiple tables.
 */
public class Address {
    public int streetNum;
    public String streetName1;
    public String streetName2;
    public String streetType;
    public String suiteNum;
    public String city;
    public String county;
    public String state;
    public int zip;
    public int plus4;
    public String country;
    public int gmtOffset;

    public String getFullStreetName() {
        if (streetName2 != null && !streetName2.isEmpty()) {
            return streetName1 + " " + streetName2;
        }
        return streetName1;
    }

    public String getFormattedZip() {
        return String.format("%05d", zip);
    }

    public String getFullZip() {
        return String.format("%05d-%04d", zip, plus4);
    }
}
