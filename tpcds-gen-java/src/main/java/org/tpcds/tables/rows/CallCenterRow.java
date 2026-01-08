package org.tpcds.tables.rows;

import org.tpcds.types.DsDecimal;
import org.tpcds.util.Address;

public class CallCenterRow {
    public long cc_call_center_sk;
    public String cc_call_center_id;
    public Integer cc_rec_start_date_sk;
    public Integer cc_rec_end_date_sk;
    public Long cc_closed_date_sk;
    public Long cc_open_date_sk;
    public String cc_name;
    public String cc_class;
    public int cc_employees;
    public int cc_sq_ft;
    public String cc_hours;
    public String cc_manager;
    public int cc_mkt_id;
    public String cc_mkt_class;
    public String cc_mkt_desc;
    public String cc_market_manager;
    public Long cc_division;
    public String cc_division_name;
    public Long cc_company;
    public String cc_company_name;
    public Address cc_address;
    public DsDecimal cc_tax_percentage;
}
