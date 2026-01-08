package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.StoreSalesRow;
import com.streever.utils.hive.testbench.tpcds.types.DsDecimal;
import com.streever.utils.hive.testbench.tpcds.util.Scaling;

public class StoreSalesTable extends BaseTable<StoreSalesRow> {

    private static final String[] COLUMN_NAMES = {
        "ss_sold_date_sk", "ss_sold_time_sk", "ss_item_sk", "ss_customer_sk",
        "ss_cdemo_sk", "ss_hdemo_sk", "ss_addr_sk", "ss_store_sk", "ss_promo_sk",
        "ss_ticket_number", "ss_quantity", "ss_wholesale_cost", "ss_list_price",
        "ss_sales_price", "ss_ext_discount_amt", "ss_ext_sales_price",
        "ss_ext_wholesale_cost", "ss_ext_list_price", "ss_ext_tax", "ss_coupon_amt",
        "ss_net_paid", "ss_net_paid_inc_tax", "ss_net_profit"
    };

    private static final int START_DATE = 2450815;  // 1998-01-01
    private static final int DATE_RANGE = 365 * 5;  // 5 years

    public StoreSalesTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.STORE_SALES, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.STORE_SALES, scaleFactor);
    }

    @Override
    public StoreSalesRow buildRow(long rowNumber) {
        StoreSalesRow r = new StoreSalesRow();

        // Get dimension counts
        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        long customerCount = Scaling.getRowCount(TableId.CUSTOMER, scaleFactor);
        long storeCount = Scaling.getRowCount(TableId.STORE, scaleFactor);
        long promoCount = Scaling.getRowCount(TableId.PROMOTION, scaleFactor);
        long cdemoCount = 1920800L;
        long hdemoCount = 7200L;
        long addrCount = 50000L * scaleFactor;

        // Date and time
        r.ss_sold_date_sk = START_DATE + (rowNumber % DATE_RANGE);
        r.ss_sold_time_sk = rowNumber % 86400;

        // Foreign keys
        r.ss_item_sk = (rowNumber % itemCount) + 1;
        r.ss_customer_sk = (rowNumber % customerCount) + 1;
        r.ss_store_sk = (rowNumber % storeCount) + 1;

        // Nullable foreign keys
        if (rowNumber % 100 < 95) {
            r.ss_cdemo_sk = (rowNumber % cdemoCount) + 1;
        }
        if (rowNumber % 100 < 90) {
            r.ss_hdemo_sk = (rowNumber % hdemoCount) + 1;
        }
        if (rowNumber % 100 < 98) {
            r.ss_addr_sk = (rowNumber % addrCount) + 1;
        }
        if (rowNumber % 100 < 50) {
            r.ss_promo_sk = (rowNumber % promoCount) + 1;
        }

        // Ticket number (groups items in same transaction)
        r.ss_ticket_number = rowNumber / 10 + 1;

        // Quantity
        r.ss_quantity = (int) ((rowNumber % 100) + 1);

        // Prices (in cents)
        long wholesaleCost = 100 + (rowNumber % 9900);
        long listPrice = wholesaleCost + (rowNumber % 5000);
        long salesPrice = listPrice - (rowNumber % 1000);

        r.ss_wholesale_cost = new DsDecimal(wholesaleCost, 2, 5);
        r.ss_list_price = new DsDecimal(listPrice, 2, 5);
        r.ss_sales_price = new DsDecimal(salesPrice, 2, 5);

        // Extended amounts
        long extWholesale = wholesaleCost * r.ss_quantity;
        long extList = listPrice * r.ss_quantity;
        long extSales = salesPrice * r.ss_quantity;
        long extDiscount = extList - extSales;
        long tax = extSales / 10;  // 10% tax
        long coupon = (rowNumber % 100 < 30) ? (extSales / 20) : 0;

        r.ss_ext_wholesale_cost = new DsDecimal(extWholesale, 2, 7);
        r.ss_ext_list_price = new DsDecimal(extList, 2, 7);
        r.ss_ext_sales_price = new DsDecimal(extSales, 2, 7);
        r.ss_ext_discount_amt = new DsDecimal(extDiscount, 2, 7);
        r.ss_ext_tax = new DsDecimal(tax, 2, 7);
        r.ss_coupon_amt = new DsDecimal(coupon, 2, 7);

        // Net amounts
        long netPaid = extSales - coupon;
        long netPaidIncTax = netPaid + tax;
        long netProfit = netPaid - extWholesale;

        r.ss_net_paid = new DsDecimal(netPaid, 2, 7);
        r.ss_net_paid_inc_tax = new DsDecimal(netPaidIncTax, 2, 7);
        r.ss_net_profit = new DsDecimal(netProfit, 2, 7);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(StoreSalesRow r, String delimiter) {
        return join(delimiter,
            r.ss_sold_date_sk,
            r.ss_sold_time_sk,
            r.ss_item_sk,
            r.ss_customer_sk,
            r.ss_cdemo_sk,
            r.ss_hdemo_sk,
            r.ss_addr_sk,
            r.ss_store_sk,
            r.ss_promo_sk,
            r.ss_ticket_number,
            r.ss_quantity,
            r.ss_wholesale_cost,
            r.ss_list_price,
            r.ss_sales_price,
            r.ss_ext_discount_amt,
            r.ss_ext_sales_price,
            r.ss_ext_wholesale_cost,
            r.ss_ext_list_price,
            r.ss_ext_tax,
            r.ss_coupon_amt,
            r.ss_net_paid,
            r.ss_net_paid_inc_tax,
            r.ss_net_profit
        );
    }
}
