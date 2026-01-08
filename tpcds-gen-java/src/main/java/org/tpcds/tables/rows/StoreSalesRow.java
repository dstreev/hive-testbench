package org.tpcds.tables.rows;

import org.tpcds.types.DsDecimal;

public class StoreSalesRow {
    public long ss_sold_date_sk;
    public long ss_sold_time_sk;
    public long ss_item_sk;
    public long ss_customer_sk;
    public Long ss_cdemo_sk;
    public Long ss_hdemo_sk;
    public Long ss_addr_sk;
    public long ss_store_sk;
    public Long ss_promo_sk;
    public long ss_ticket_number;
    public int ss_quantity;
    public DsDecimal ss_wholesale_cost;
    public DsDecimal ss_list_price;
    public DsDecimal ss_sales_price;
    public DsDecimal ss_ext_discount_amt;
    public DsDecimal ss_ext_sales_price;
    public DsDecimal ss_ext_wholesale_cost;
    public DsDecimal ss_ext_list_price;
    public DsDecimal ss_ext_tax;
    public DsDecimal ss_coupon_amt;
    public DsDecimal ss_net_paid;
    public DsDecimal ss_net_paid_inc_tax;
    public DsDecimal ss_net_profit;
}
