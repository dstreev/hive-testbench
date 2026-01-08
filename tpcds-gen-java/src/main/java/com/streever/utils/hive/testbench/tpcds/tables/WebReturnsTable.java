package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.WebReturnsRow;
import com.streever.utils.hive.testbench.tpcds.types.DsDecimal;
import com.streever.utils.hive.testbench.tpcds.util.Scaling;

public class WebReturnsTable extends BaseTable<WebReturnsRow> {

    private static final String[] COLUMN_NAMES = {
        "wr_returned_date_sk", "wr_returned_time_sk", "wr_item_sk",
        "wr_refunded_customer_sk", "wr_refunded_cdemo_sk", "wr_refunded_hdemo_sk",
        "wr_refunded_addr_sk", "wr_returning_customer_sk", "wr_returning_cdemo_sk",
        "wr_returning_hdemo_sk", "wr_returning_addr_sk", "wr_web_page_sk",
        "wr_reason_sk", "wr_order_number", "wr_return_quantity", "wr_return_amt",
        "wr_return_tax", "wr_return_amt_inc_tax", "wr_fee", "wr_return_ship_cost",
        "wr_refunded_cash", "wr_reversed_charge", "wr_account_credit", "wr_net_loss"
    };

    private static final int START_DATE = 2450815;
    private static final int DATE_RANGE = 365 * 5;

    public WebReturnsTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.WEB_RETURNS, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.WEB_RETURNS, scaleFactor);
    }

    @Override
    public WebReturnsRow buildRow(long rowNumber) {
        WebReturnsRow r = new WebReturnsRow();

        // Get dimension counts
        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        long customerCount = Scaling.getRowCount(TableId.CUSTOMER, scaleFactor);
        long webPageCount = Scaling.getRowCount(TableId.WEB_PAGE, scaleFactor);
        long reasonCount = distMgr.distSize("return_reasons");
        long cdemoCount = 1920800L;
        long hdemoCount = 7200L;
        long addrCount = 50000L * scaleFactor;

        // Return date
        r.wr_returned_date_sk = START_DATE + (rowNumber % DATE_RANGE) + 7 + (rowNumber % 30);
        r.wr_returned_time_sk = rowNumber % 86400;

        r.wr_item_sk = (rowNumber % itemCount) + 1;

        // Refunded customer
        r.wr_refunded_customer_sk = (rowNumber % customerCount) + 1;
        if (rowNumber % 100 < 95) {
            r.wr_refunded_cdemo_sk = (rowNumber % cdemoCount) + 1;
        }
        if (rowNumber % 100 < 90) {
            r.wr_refunded_hdemo_sk = (rowNumber % hdemoCount) + 1;
        }
        if (rowNumber % 100 < 98) {
            r.wr_refunded_addr_sk = (rowNumber % addrCount) + 1;
        }

        // Returning customer
        if (rowNumber % 2 == 0) {
            r.wr_returning_customer_sk = r.wr_refunded_customer_sk;
            r.wr_returning_cdemo_sk = r.wr_refunded_cdemo_sk;
            r.wr_returning_hdemo_sk = r.wr_refunded_hdemo_sk;
            r.wr_returning_addr_sk = r.wr_refunded_addr_sk;
        } else {
            r.wr_returning_customer_sk = ((rowNumber + 1) % customerCount) + 1;
            if (rowNumber % 100 < 95) {
                r.wr_returning_cdemo_sk = ((rowNumber + 1) % cdemoCount) + 1;
            }
            if (rowNumber % 100 < 90) {
                r.wr_returning_hdemo_sk = ((rowNumber + 1) % hdemoCount) + 1;
            }
            if (rowNumber % 100 < 98) {
                r.wr_returning_addr_sk = ((rowNumber + 1) % addrCount) + 1;
            }
        }

        // Other foreign keys
        if (webPageCount > 0) {
            r.wr_web_page_sk = (rowNumber % webPageCount) + 1;
        }
        if (reasonCount > 0) {
            r.wr_reason_sk = (rowNumber % reasonCount) + 1;
        }

        r.wr_order_number = rowNumber / 5 + 1;
        r.wr_return_quantity = (int) ((rowNumber % 10) + 1);

        // Amounts
        long returnAmt = 100 + (rowNumber % 9900);
        long tax = returnAmt / 10;
        long fee = 500;
        long shipCost = 200 + (rowNumber % 800);

        r.wr_return_amt = new DsDecimal(returnAmt * r.wr_return_quantity, 2, 7);
        r.wr_return_tax = new DsDecimal(tax * r.wr_return_quantity, 2, 7);
        r.wr_return_amt_inc_tax = new DsDecimal((returnAmt + tax) * r.wr_return_quantity, 2, 7);
        r.wr_fee = new DsDecimal(fee, 2, 5);
        r.wr_return_ship_cost = new DsDecimal(shipCost, 2, 5);

        long totalRefund = returnAmt * r.wr_return_quantity - fee;
        long refundedCash = totalRefund / 2;
        long reversedCharge = totalRefund / 4;
        long accountCredit = totalRefund - refundedCash - reversedCharge;

        r.wr_refunded_cash = new DsDecimal(refundedCash, 2, 7);
        r.wr_reversed_charge = new DsDecimal(reversedCharge, 2, 7);
        r.wr_account_credit = new DsDecimal(accountCredit, 2, 7);
        r.wr_net_loss = new DsDecimal(shipCost + fee, 2, 7);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(WebReturnsRow r, String delimiter) {
        return join(delimiter,
            r.wr_returned_date_sk,
            r.wr_returned_time_sk,
            r.wr_item_sk,
            r.wr_refunded_customer_sk,
            r.wr_refunded_cdemo_sk,
            r.wr_refunded_hdemo_sk,
            r.wr_refunded_addr_sk,
            r.wr_returning_customer_sk,
            r.wr_returning_cdemo_sk,
            r.wr_returning_hdemo_sk,
            r.wr_returning_addr_sk,
            r.wr_web_page_sk,
            r.wr_reason_sk,
            r.wr_order_number,
            r.wr_return_quantity,
            r.wr_return_amt,
            r.wr_return_tax,
            r.wr_return_amt_inc_tax,
            r.wr_fee,
            r.wr_return_ship_cost,
            r.wr_refunded_cash,
            r.wr_reversed_charge,
            r.wr_account_credit,
            r.wr_net_loss
        );
    }
}
