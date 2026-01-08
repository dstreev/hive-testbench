package com.streever.utils.hive.testbench.tpcds.tables.rows;

import com.streever.utils.hive.testbench.tpcds.types.DsDecimal;

public class CatalogReturnsRow {
    public long cr_returned_date_sk;
    public Long cr_returned_time_sk;
    public long cr_item_sk;
    public Long cr_refunded_customer_sk;
    public Long cr_refunded_cdemo_sk;
    public Long cr_refunded_hdemo_sk;
    public Long cr_refunded_addr_sk;
    public Long cr_returning_customer_sk;
    public Long cr_returning_cdemo_sk;
    public Long cr_returning_hdemo_sk;
    public Long cr_returning_addr_sk;
    public Long cr_call_center_sk;
    public Long cr_catalog_page_sk;
    public Long cr_ship_mode_sk;
    public Long cr_warehouse_sk;
    public Long cr_reason_sk;
    public long cr_order_number;
    public int cr_return_quantity;
    public DsDecimal cr_return_amount;
    public DsDecimal cr_return_tax;
    public DsDecimal cr_return_amt_inc_tax;
    public DsDecimal cr_fee;
    public DsDecimal cr_return_ship_cost;
    public DsDecimal cr_refunded_cash;
    public DsDecimal cr_reversed_charge;
    public DsDecimal cr_store_credit;
    public DsDecimal cr_net_loss;
}
