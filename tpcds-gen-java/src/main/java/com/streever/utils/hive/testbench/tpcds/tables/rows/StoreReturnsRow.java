package com.streever.utils.hive.testbench.tpcds.tables.rows;

import com.streever.utils.hive.testbench.tpcds.types.DsDecimal;

public class StoreReturnsRow {
    public long sr_returned_date_sk;
    public Long sr_return_time_sk;
    public long sr_item_sk;
    public long sr_customer_sk;
    public Long sr_cdemo_sk;
    public Long sr_hdemo_sk;
    public Long sr_addr_sk;
    public long sr_store_sk;
    public Long sr_reason_sk;
    public long sr_ticket_number;
    public int sr_return_quantity;
    public DsDecimal sr_return_amt;
    public DsDecimal sr_return_tax;
    public DsDecimal sr_return_amt_inc_tax;
    public DsDecimal sr_fee;
    public DsDecimal sr_return_ship_cost;
    public DsDecimal sr_refunded_cash;
    public DsDecimal sr_reversed_charge;
    public DsDecimal sr_store_credit;
    public DsDecimal sr_net_loss;
}
