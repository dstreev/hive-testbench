package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.CatalogReturnsRow;
import org.tpcds.types.DsDecimal;
import org.tpcds.util.Scaling;

public class CatalogReturnsTable extends BaseTable<CatalogReturnsRow> {

    private static final String[] COLUMN_NAMES = {
        "cr_returned_date_sk", "cr_returned_time_sk", "cr_item_sk",
        "cr_refunded_customer_sk", "cr_refunded_cdemo_sk", "cr_refunded_hdemo_sk",
        "cr_refunded_addr_sk", "cr_returning_customer_sk", "cr_returning_cdemo_sk",
        "cr_returning_hdemo_sk", "cr_returning_addr_sk", "cr_call_center_sk",
        "cr_catalog_page_sk", "cr_ship_mode_sk", "cr_warehouse_sk", "cr_reason_sk",
        "cr_order_number", "cr_return_quantity", "cr_return_amount", "cr_return_tax",
        "cr_return_amt_inc_tax", "cr_fee", "cr_return_ship_cost", "cr_refunded_cash",
        "cr_reversed_charge", "cr_store_credit", "cr_net_loss"
    };

    private static final int START_DATE = 2450815;
    private static final int DATE_RANGE = 365 * 5;

    public CatalogReturnsTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.CATALOG_RETURNS, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.CATALOG_RETURNS, scaleFactor);
    }

    @Override
    public CatalogReturnsRow buildRow(long rowNumber) {
        CatalogReturnsRow r = new CatalogReturnsRow();

        // Get dimension counts
        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        long customerCount = Scaling.getRowCount(TableId.CUSTOMER, scaleFactor);
        long callCenterCount = Scaling.getRowCount(TableId.CALL_CENTER, scaleFactor);
        long catalogPageCount = Scaling.getRowCount(TableId.CATALOG_PAGE, scaleFactor);
        long shipModeCount = 20L;
        long warehouseCount = Scaling.getRowCount(TableId.WAREHOUSE, scaleFactor);
        long reasonCount = distMgr.distSize("return_reasons");
        long cdemoCount = 1920800L;
        long hdemoCount = 7200L;
        long addrCount = 50000L * scaleFactor;

        // Return date
        r.cr_returned_date_sk = START_DATE + (rowNumber % DATE_RANGE) + 7 + (rowNumber % 30);
        r.cr_returned_time_sk = rowNumber % 86400;

        r.cr_item_sk = (rowNumber % itemCount) + 1;

        // Refunded customer
        r.cr_refunded_customer_sk = (rowNumber % customerCount) + 1;
        if (rowNumber % 100 < 95) {
            r.cr_refunded_cdemo_sk = (rowNumber % cdemoCount) + 1;
        }
        if (rowNumber % 100 < 90) {
            r.cr_refunded_hdemo_sk = (rowNumber % hdemoCount) + 1;
        }
        if (rowNumber % 100 < 98) {
            r.cr_refunded_addr_sk = (rowNumber % addrCount) + 1;
        }

        // Returning customer (sometimes same)
        if (rowNumber % 2 == 0) {
            r.cr_returning_customer_sk = r.cr_refunded_customer_sk;
            r.cr_returning_cdemo_sk = r.cr_refunded_cdemo_sk;
            r.cr_returning_hdemo_sk = r.cr_refunded_hdemo_sk;
            r.cr_returning_addr_sk = r.cr_refunded_addr_sk;
        } else {
            r.cr_returning_customer_sk = ((rowNumber + 1) % customerCount) + 1;
            if (rowNumber % 100 < 95) {
                r.cr_returning_cdemo_sk = ((rowNumber + 1) % cdemoCount) + 1;
            }
            if (rowNumber % 100 < 90) {
                r.cr_returning_hdemo_sk = ((rowNumber + 1) % hdemoCount) + 1;
            }
            if (rowNumber % 100 < 98) {
                r.cr_returning_addr_sk = ((rowNumber + 1) % addrCount) + 1;
            }
        }

        // Other foreign keys
        if (callCenterCount > 0) {
            r.cr_call_center_sk = (rowNumber % callCenterCount) + 1;
        }
        if (catalogPageCount > 0) {
            r.cr_catalog_page_sk = (rowNumber % catalogPageCount) + 1;
        }
        r.cr_ship_mode_sk = (rowNumber % shipModeCount) + 1;
        if (warehouseCount > 0) {
            r.cr_warehouse_sk = (rowNumber % warehouseCount) + 1;
        }
        if (reasonCount > 0) {
            r.cr_reason_sk = (rowNumber % reasonCount) + 1;
        }

        r.cr_order_number = rowNumber / 5 + 1;
        r.cr_return_quantity = (int) ((rowNumber % 10) + 1);

        // Amounts
        long returnAmt = 100 + (rowNumber % 9900);
        long tax = returnAmt / 10;
        long fee = 500;
        long shipCost = 200 + (rowNumber % 800);

        r.cr_return_amount = new DsDecimal(returnAmt * r.cr_return_quantity, 2, 7);
        r.cr_return_tax = new DsDecimal(tax * r.cr_return_quantity, 2, 7);
        r.cr_return_amt_inc_tax = new DsDecimal((returnAmt + tax) * r.cr_return_quantity, 2, 7);
        r.cr_fee = new DsDecimal(fee, 2, 5);
        r.cr_return_ship_cost = new DsDecimal(shipCost, 2, 5);

        long totalRefund = returnAmt * r.cr_return_quantity - fee;
        long refundedCash = totalRefund / 2;
        long reversedCharge = totalRefund / 4;
        long storeCredit = totalRefund - refundedCash - reversedCharge;

        r.cr_refunded_cash = new DsDecimal(refundedCash, 2, 7);
        r.cr_reversed_charge = new DsDecimal(reversedCharge, 2, 7);
        r.cr_store_credit = new DsDecimal(storeCredit, 2, 7);
        r.cr_net_loss = new DsDecimal(shipCost + fee, 2, 7);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(CatalogReturnsRow r, String delimiter) {
        return join(delimiter,
            r.cr_returned_date_sk,
            r.cr_returned_time_sk,
            r.cr_item_sk,
            r.cr_refunded_customer_sk,
            r.cr_refunded_cdemo_sk,
            r.cr_refunded_hdemo_sk,
            r.cr_refunded_addr_sk,
            r.cr_returning_customer_sk,
            r.cr_returning_cdemo_sk,
            r.cr_returning_hdemo_sk,
            r.cr_returning_addr_sk,
            r.cr_call_center_sk,
            r.cr_catalog_page_sk,
            r.cr_ship_mode_sk,
            r.cr_warehouse_sk,
            r.cr_reason_sk,
            r.cr_order_number,
            r.cr_return_quantity,
            r.cr_return_amount,
            r.cr_return_tax,
            r.cr_return_amt_inc_tax,
            r.cr_fee,
            r.cr_return_ship_cost,
            r.cr_refunded_cash,
            r.cr_reversed_charge,
            r.cr_store_credit,
            r.cr_net_loss
        );
    }
}
