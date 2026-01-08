package com.streever.utils.hive.testbench.tpcds.tables.rows;

import com.streever.utils.hive.testbench.tpcds.types.DsDecimal;

public class WebSalesRow {
    public long ws_sold_date_sk;
    public long ws_sold_time_sk;
    public Long ws_ship_date_sk;
    public long ws_item_sk;
    public long ws_bill_customer_sk;
    public Long ws_bill_cdemo_sk;
    public Long ws_bill_hdemo_sk;
    public Long ws_bill_addr_sk;
    public Long ws_ship_customer_sk;
    public Long ws_ship_cdemo_sk;
    public Long ws_ship_hdemo_sk;
    public Long ws_ship_addr_sk;
    public Long ws_web_page_sk;
    public Long ws_web_site_sk;
    public Long ws_ship_mode_sk;
    public Long ws_warehouse_sk;
    public Long ws_promo_sk;
    public long ws_order_number;
    public int ws_quantity;
    public DsDecimal ws_wholesale_cost;
    public DsDecimal ws_list_price;
    public DsDecimal ws_sales_price;
    public DsDecimal ws_ext_discount_amt;
    public DsDecimal ws_ext_sales_price;
    public DsDecimal ws_ext_wholesale_cost;
    public DsDecimal ws_ext_list_price;
    public DsDecimal ws_ext_tax;
    public DsDecimal ws_coupon_amt;
    public DsDecimal ws_ext_ship_cost;
    public DsDecimal ws_net_paid;
    public DsDecimal ws_net_paid_inc_tax;
    public DsDecimal ws_net_paid_inc_ship;
    public DsDecimal ws_net_paid_inc_ship_tax;
    public DsDecimal ws_net_profit;
}
