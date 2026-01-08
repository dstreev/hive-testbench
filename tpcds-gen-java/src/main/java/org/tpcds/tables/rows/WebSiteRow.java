package org.tpcds.tables.rows;

import org.tpcds.types.DsDecimal;
import org.tpcds.util.Address;

public class WebSiteRow {
    public long web_site_sk;
    public String web_site_id;
    public Integer web_rec_start_date_sk;
    public Integer web_rec_end_date_sk;
    public String web_name;
    public Long web_open_date_sk;
    public Long web_close_date_sk;
    public String web_class;
    public String web_manager;
    public int web_mkt_id;
    public String web_mkt_class;
    public String web_mkt_desc;
    public String web_market_manager;
    public Long web_company_id;
    public String web_company_name;
    public Address web_address;
    public DsDecimal web_tax_percentage;
}
