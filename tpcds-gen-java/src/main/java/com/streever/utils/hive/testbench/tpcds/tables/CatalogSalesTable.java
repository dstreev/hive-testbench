package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.CatalogSalesRow;
import com.streever.utils.hive.testbench.tpcds.types.DsDecimal;
import com.streever.utils.hive.testbench.tpcds.util.Scaling;

public class CatalogSalesTable extends BaseTable<CatalogSalesRow> {

    private static final String[] COLUMN_NAMES = {
        "cs_sold_date_sk", "cs_sold_time_sk", "cs_ship_date_sk",
        "cs_bill_customer_sk", "cs_bill_cdemo_sk", "cs_bill_hdemo_sk", "cs_bill_addr_sk",
        "cs_ship_customer_sk", "cs_ship_cdemo_sk", "cs_ship_hdemo_sk", "cs_ship_addr_sk",
        "cs_call_center_sk", "cs_catalog_page_sk", "cs_ship_mode_sk", "cs_warehouse_sk",
        "cs_item_sk", "cs_promo_sk", "cs_order_number", "cs_quantity",
        "cs_wholesale_cost", "cs_list_price", "cs_sales_price",
        "cs_ext_discount_amt", "cs_ext_sales_price", "cs_ext_wholesale_cost",
        "cs_ext_list_price", "cs_ext_tax", "cs_coupon_amt", "cs_ext_ship_cost",
        "cs_net_paid", "cs_net_paid_inc_tax", "cs_net_paid_inc_ship",
        "cs_net_paid_inc_ship_tax", "cs_net_profit"
    };

    private static final int START_DATE = 2450815;  // 1998-01-01
    private static final int DATE_RANGE = 365 * 5;  // 5 years

    public CatalogSalesTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.CATALOG_SALES, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.CATALOG_SALES, scaleFactor);
    }

    @Override
    public CatalogSalesRow buildRow(long rowNumber) {
        CatalogSalesRow r = new CatalogSalesRow();

        // Get dimension counts
        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        long customerCount = Scaling.getRowCount(TableId.CUSTOMER, scaleFactor);
        long callCenterCount = Scaling.getRowCount(TableId.CALL_CENTER, scaleFactor);
        long catalogPageCount = Scaling.getRowCount(TableId.CATALOG_PAGE, scaleFactor);
        long shipModeCount = 20L;
        long warehouseCount = Scaling.getRowCount(TableId.WAREHOUSE, scaleFactor);
        long promoCount = Scaling.getRowCount(TableId.PROMOTION, scaleFactor);
        long cdemoCount = 1920800L;
        long hdemoCount = 7200L;
        long addrCount = 50000L * scaleFactor;

        // Date and time
        r.cs_sold_date_sk = START_DATE + (rowNumber % DATE_RANGE);
        r.cs_sold_time_sk = rowNumber % 86400;
        r.cs_ship_date_sk = r.cs_sold_date_sk + 1 + (rowNumber % 7);

        // Bill-to customer
        r.cs_bill_customer_sk = (rowNumber % customerCount) + 1;
        if (rowNumber % 100 < 95) {
            r.cs_bill_cdemo_sk = (rowNumber % cdemoCount) + 1;
        }
        if (rowNumber % 100 < 90) {
            r.cs_bill_hdemo_sk = (rowNumber % hdemoCount) + 1;
        }
        if (rowNumber % 100 < 98) {
            r.cs_bill_addr_sk = (rowNumber % addrCount) + 1;
        }

        // Ship-to customer (sometimes same as bill-to)
        if (rowNumber % 3 == 0) {
            r.cs_ship_customer_sk = r.cs_bill_customer_sk;
            r.cs_ship_cdemo_sk = r.cs_bill_cdemo_sk;
            r.cs_ship_hdemo_sk = r.cs_bill_hdemo_sk;
            r.cs_ship_addr_sk = r.cs_bill_addr_sk;
        } else {
            r.cs_ship_customer_sk = ((rowNumber + 1) % customerCount) + 1;
            if (rowNumber % 100 < 95) {
                r.cs_ship_cdemo_sk = ((rowNumber + 1) % cdemoCount) + 1;
            }
            if (rowNumber % 100 < 90) {
                r.cs_ship_hdemo_sk = ((rowNumber + 1) % hdemoCount) + 1;
            }
            if (rowNumber % 100 < 98) {
                r.cs_ship_addr_sk = ((rowNumber + 1) % addrCount) + 1;
            }
        }

        // Other foreign keys
        if (callCenterCount > 0) {
            r.cs_call_center_sk = (rowNumber % callCenterCount) + 1;
        }
        if (catalogPageCount > 0) {
            r.cs_catalog_page_sk = (rowNumber % catalogPageCount) + 1;
        }
        r.cs_ship_mode_sk = (rowNumber % shipModeCount) + 1;
        if (warehouseCount > 0) {
            r.cs_warehouse_sk = (rowNumber % warehouseCount) + 1;
        }

        r.cs_item_sk = (rowNumber % itemCount) + 1;
        if (rowNumber % 100 < 50) {
            r.cs_promo_sk = (rowNumber % promoCount) + 1;
        }

        // Order number
        r.cs_order_number = rowNumber / 10 + 1;

        // Quantity
        r.cs_quantity = (int) ((rowNumber % 100) + 1);

        // Prices (in cents)
        long wholesaleCost = 100 + (rowNumber % 9900);
        long listPrice = wholesaleCost + (rowNumber % 5000);
        long salesPrice = listPrice - (rowNumber % 1000);
        long shipCost = 200 + (rowNumber % 800);

        r.cs_wholesale_cost = new DsDecimal(wholesaleCost, 2, 5);
        r.cs_list_price = new DsDecimal(listPrice, 2, 5);
        r.cs_sales_price = new DsDecimal(salesPrice, 2, 5);

        // Extended amounts
        long extWholesale = wholesaleCost * r.cs_quantity;
        long extList = listPrice * r.cs_quantity;
        long extSales = salesPrice * r.cs_quantity;
        long extDiscount = extList - extSales;
        long tax = extSales / 10;
        long coupon = (rowNumber % 100 < 30) ? (extSales / 20) : 0;
        long extShip = shipCost * r.cs_quantity;

        r.cs_ext_wholesale_cost = new DsDecimal(extWholesale, 2, 7);
        r.cs_ext_list_price = new DsDecimal(extList, 2, 7);
        r.cs_ext_sales_price = new DsDecimal(extSales, 2, 7);
        r.cs_ext_discount_amt = new DsDecimal(extDiscount, 2, 7);
        r.cs_ext_tax = new DsDecimal(tax, 2, 7);
        r.cs_coupon_amt = new DsDecimal(coupon, 2, 7);
        r.cs_ext_ship_cost = new DsDecimal(extShip, 2, 7);

        // Net amounts
        long netPaid = extSales - coupon;
        long netPaidIncTax = netPaid + tax;
        long netPaidIncShip = netPaid + extShip;
        long netPaidIncShipTax = netPaidIncShip + tax;
        long netProfit = netPaid - extWholesale;

        r.cs_net_paid = new DsDecimal(netPaid, 2, 7);
        r.cs_net_paid_inc_tax = new DsDecimal(netPaidIncTax, 2, 7);
        r.cs_net_paid_inc_ship = new DsDecimal(netPaidIncShip, 2, 7);
        r.cs_net_paid_inc_ship_tax = new DsDecimal(netPaidIncShipTax, 2, 7);
        r.cs_net_profit = new DsDecimal(netProfit, 2, 7);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(CatalogSalesRow r, String delimiter) {
        return join(delimiter,
            r.cs_sold_date_sk,
            r.cs_sold_time_sk,
            r.cs_ship_date_sk,
            r.cs_bill_customer_sk,
            r.cs_bill_cdemo_sk,
            r.cs_bill_hdemo_sk,
            r.cs_bill_addr_sk,
            r.cs_ship_customer_sk,
            r.cs_ship_cdemo_sk,
            r.cs_ship_hdemo_sk,
            r.cs_ship_addr_sk,
            r.cs_call_center_sk,
            r.cs_catalog_page_sk,
            r.cs_ship_mode_sk,
            r.cs_warehouse_sk,
            r.cs_item_sk,
            r.cs_promo_sk,
            r.cs_order_number,
            r.cs_quantity,
            r.cs_wholesale_cost,
            r.cs_list_price,
            r.cs_sales_price,
            r.cs_ext_discount_amt,
            r.cs_ext_sales_price,
            r.cs_ext_wholesale_cost,
            r.cs_ext_list_price,
            r.cs_ext_tax,
            r.cs_coupon_amt,
            r.cs_ext_ship_cost,
            r.cs_net_paid,
            r.cs_net_paid_inc_tax,
            r.cs_net_paid_inc_ship,
            r.cs_net_paid_inc_ship_tax,
            r.cs_net_profit
        );
    }
}
