package org.tpcds.tables.rows;

import org.tpcds.util.Address;
import org.tpcds.types.DsDecimal;

public class StoreRow {
    public long s_store_sk;
    public String s_store_id;
    public Integer s_rec_start_date_sk;
    public Integer s_rec_end_date_sk;
    public Long s_closed_date_sk;
    public String s_store_name;
    public int s_number_employees;
    public int s_floor_space;
    public String s_hours;
    public String s_manager;
    public int s_market_id;
    public String s_geography_class;
    public String s_market_desc;
    public String s_market_manager;
    public Long s_division_id;
    public String s_division_name;
    public Long s_company_id;
    public String s_company_name;
    public Address s_address;
    public DsDecimal s_tax_percentage;
}
