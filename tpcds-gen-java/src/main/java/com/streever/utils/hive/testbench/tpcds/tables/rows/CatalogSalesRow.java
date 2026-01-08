package com.streever.utils.hive.testbench.tpcds.tables.rows;

import com.streever.utils.hive.testbench.tpcds.types.DsDecimal;

public class CatalogSalesRow {
    public long cs_sold_date_sk;
    public long cs_sold_time_sk;
    public Long cs_ship_date_sk;
    public long cs_bill_customer_sk;
    public Long cs_bill_cdemo_sk;
    public Long cs_bill_hdemo_sk;
    public Long cs_bill_addr_sk;
    public Long cs_ship_customer_sk;
    public Long cs_ship_cdemo_sk;
    public Long cs_ship_hdemo_sk;
    public Long cs_ship_addr_sk;
    public Long cs_call_center_sk;
    public Long cs_catalog_page_sk;
    public Long cs_ship_mode_sk;
    public Long cs_warehouse_sk;
    public long cs_item_sk;
    public Long cs_promo_sk;
    public long cs_order_number;
    public int cs_quantity;
    public DsDecimal cs_wholesale_cost;
    public DsDecimal cs_list_price;
    public DsDecimal cs_sales_price;
    public DsDecimal cs_ext_discount_amt;
    public DsDecimal cs_ext_sales_price;
    public DsDecimal cs_ext_wholesale_cost;
    public DsDecimal cs_ext_list_price;
    public DsDecimal cs_ext_tax;
    public DsDecimal cs_coupon_amt;
    public DsDecimal cs_ext_ship_cost;
    public DsDecimal cs_net_paid;
    public DsDecimal cs_net_paid_inc_tax;
    public DsDecimal cs_net_paid_inc_ship;
    public DsDecimal cs_net_paid_inc_ship_tax;
    public DsDecimal cs_net_profit;
}
