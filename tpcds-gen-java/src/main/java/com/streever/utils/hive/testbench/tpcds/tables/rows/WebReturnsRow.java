package com.streever.utils.hive.testbench.tpcds.tables.rows;

import com.streever.utils.hive.testbench.tpcds.types.DsDecimal;

public class WebReturnsRow {
    public long wr_returned_date_sk;
    public Long wr_returned_time_sk;
    public long wr_item_sk;
    public Long wr_refunded_customer_sk;
    public Long wr_refunded_cdemo_sk;
    public Long wr_refunded_hdemo_sk;
    public Long wr_refunded_addr_sk;
    public Long wr_returning_customer_sk;
    public Long wr_returning_cdemo_sk;
    public Long wr_returning_hdemo_sk;
    public Long wr_returning_addr_sk;
    public Long wr_web_page_sk;
    public Long wr_reason_sk;
    public long wr_order_number;
    public int wr_return_quantity;
    public DsDecimal wr_return_amt;
    public DsDecimal wr_return_tax;
    public DsDecimal wr_return_amt_inc_tax;
    public DsDecimal wr_fee;
    public DsDecimal wr_return_ship_cost;
    public DsDecimal wr_refunded_cash;
    public DsDecimal wr_reversed_charge;
    public DsDecimal wr_account_credit;
    public DsDecimal wr_net_loss;
}
