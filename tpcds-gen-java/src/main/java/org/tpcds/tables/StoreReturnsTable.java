package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.StoreReturnsRow;
import org.tpcds.types.DsDecimal;
import org.tpcds.util.Scaling;

public class StoreReturnsTable extends BaseTable<StoreReturnsRow> {

    private static final String[] COLUMN_NAMES = {
        "sr_returned_date_sk", "sr_return_time_sk", "sr_item_sk", "sr_customer_sk",
        "sr_cdemo_sk", "sr_hdemo_sk", "sr_addr_sk", "sr_store_sk", "sr_reason_sk",
        "sr_ticket_number", "sr_return_quantity", "sr_return_amt", "sr_return_tax",
        "sr_return_amt_inc_tax", "sr_fee", "sr_return_ship_cost", "sr_refunded_cash",
        "sr_reversed_charge", "sr_store_credit", "sr_net_loss"
    };

    private static final int START_DATE = 2450815;  // 1998-01-01
    private static final int DATE_RANGE = 365 * 5;  // 5 years

    public StoreReturnsTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.STORE_RETURNS, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.STORE_RETURNS, scaleFactor);
    }

    @Override
    public StoreReturnsRow buildRow(long rowNumber) {
        StoreReturnsRow r = new StoreReturnsRow();

        // Get dimension counts
        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        long customerCount = Scaling.getRowCount(TableId.CUSTOMER, scaleFactor);
        long storeCount = Scaling.getRowCount(TableId.STORE, scaleFactor);
        long reasonCount = distMgr.distSize("return_reasons");
        long cdemoCount = 1920800L;
        long hdemoCount = 7200L;
        long addrCount = 50000L * scaleFactor;

        // Return date (some time after sale)
        r.sr_returned_date_sk = START_DATE + (rowNumber % DATE_RANGE) + 7 + (rowNumber % 30);
        r.sr_return_time_sk = rowNumber % 86400;

        // Foreign keys
        r.sr_item_sk = (rowNumber % itemCount) + 1;
        r.sr_customer_sk = (rowNumber % customerCount) + 1;
        r.sr_store_sk = (rowNumber % storeCount) + 1;

        // Nullable foreign keys
        if (rowNumber % 100 < 95) {
            r.sr_cdemo_sk = (rowNumber % cdemoCount) + 1;
        }
        if (rowNumber % 100 < 90) {
            r.sr_hdemo_sk = (rowNumber % hdemoCount) + 1;
        }
        if (rowNumber % 100 < 98) {
            r.sr_addr_sk = (rowNumber % addrCount) + 1;
        }
        if (reasonCount > 0) {
            r.sr_reason_sk = (rowNumber % reasonCount) + 1;
        }

        // Ticket number
        r.sr_ticket_number = rowNumber / 5 + 1;

        // Return quantity
        r.sr_return_quantity = (int) ((rowNumber % 10) + 1);

        // Amounts (in cents)
        long returnAmt = 100 + (rowNumber % 9900);
        long tax = returnAmt / 10;
        long fee = 500;  // $5 restocking fee
        long shipCost = 200 + (rowNumber % 800);

        r.sr_return_amt = new DsDecimal(returnAmt * r.sr_return_quantity, 2, 7);
        r.sr_return_tax = new DsDecimal(tax * r.sr_return_quantity, 2, 7);
        r.sr_return_amt_inc_tax = new DsDecimal((returnAmt + tax) * r.sr_return_quantity, 2, 7);
        r.sr_fee = new DsDecimal(fee, 2, 5);
        r.sr_return_ship_cost = new DsDecimal(shipCost, 2, 5);

        // How refund was given
        long totalRefund = returnAmt * r.sr_return_quantity - fee;
        long refundedCash = totalRefund / 2;
        long reversedCharge = totalRefund / 4;
        long storeCredit = totalRefund - refundedCash - reversedCharge;

        r.sr_refunded_cash = new DsDecimal(refundedCash, 2, 7);
        r.sr_reversed_charge = new DsDecimal(reversedCharge, 2, 7);
        r.sr_store_credit = new DsDecimal(storeCredit, 2, 7);

        // Net loss
        r.sr_net_loss = new DsDecimal(shipCost + fee, 2, 7);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(StoreReturnsRow r, String delimiter) {
        return join(delimiter,
            r.sr_returned_date_sk,
            r.sr_return_time_sk,
            r.sr_item_sk,
            r.sr_customer_sk,
            r.sr_cdemo_sk,
            r.sr_hdemo_sk,
            r.sr_addr_sk,
            r.sr_store_sk,
            r.sr_reason_sk,
            r.sr_ticket_number,
            r.sr_return_quantity,
            r.sr_return_amt,
            r.sr_return_tax,
            r.sr_return_amt_inc_tax,
            r.sr_fee,
            r.sr_return_ship_cost,
            r.sr_refunded_cash,
            r.sr_reversed_charge,
            r.sr_store_credit,
            r.sr_net_loss
        );
    }
}
