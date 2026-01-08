package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.WebSalesRow;
import org.tpcds.types.DsDecimal;
import org.tpcds.util.Scaling;

public class WebSalesTable extends BaseTable<WebSalesRow> {

    private static final String[] COLUMN_NAMES = {
        "ws_sold_date_sk", "ws_sold_time_sk", "ws_ship_date_sk", "ws_item_sk",
        "ws_bill_customer_sk", "ws_bill_cdemo_sk", "ws_bill_hdemo_sk", "ws_bill_addr_sk",
        "ws_ship_customer_sk", "ws_ship_cdemo_sk", "ws_ship_hdemo_sk", "ws_ship_addr_sk",
        "ws_web_page_sk", "ws_web_site_sk", "ws_ship_mode_sk", "ws_warehouse_sk",
        "ws_promo_sk", "ws_order_number", "ws_quantity",
        "ws_wholesale_cost", "ws_list_price", "ws_sales_price",
        "ws_ext_discount_amt", "ws_ext_sales_price", "ws_ext_wholesale_cost",
        "ws_ext_list_price", "ws_ext_tax", "ws_coupon_amt", "ws_ext_ship_cost",
        "ws_net_paid", "ws_net_paid_inc_tax", "ws_net_paid_inc_ship",
        "ws_net_paid_inc_ship_tax", "ws_net_profit"
    };

    private static final int START_DATE = 2450815;
    private static final int DATE_RANGE = 365 * 5;

    public WebSalesTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.WEB_SALES, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.WEB_SALES, scaleFactor);
    }

    @Override
    public WebSalesRow buildRow(long rowNumber) {
        WebSalesRow r = new WebSalesRow();

        // Get dimension counts
        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        long customerCount = Scaling.getRowCount(TableId.CUSTOMER, scaleFactor);
        long webPageCount = Scaling.getRowCount(TableId.WEB_PAGE, scaleFactor);
        long webSiteCount = Scaling.getRowCount(TableId.WEB_SITE, scaleFactor);
        long shipModeCount = 20L;
        long warehouseCount = Scaling.getRowCount(TableId.WAREHOUSE, scaleFactor);
        long promoCount = Scaling.getRowCount(TableId.PROMOTION, scaleFactor);
        long cdemoCount = 1920800L;
        long hdemoCount = 7200L;
        long addrCount = 50000L * scaleFactor;

        // Date and time
        r.ws_sold_date_sk = START_DATE + (rowNumber % DATE_RANGE);
        r.ws_sold_time_sk = rowNumber % 86400;
        r.ws_ship_date_sk = r.ws_sold_date_sk + 1 + (rowNumber % 7);

        r.ws_item_sk = (rowNumber % itemCount) + 1;

        // Bill-to customer
        r.ws_bill_customer_sk = (rowNumber % customerCount) + 1;
        if (rowNumber % 100 < 95) {
            r.ws_bill_cdemo_sk = (rowNumber % cdemoCount) + 1;
        }
        if (rowNumber % 100 < 90) {
            r.ws_bill_hdemo_sk = (rowNumber % hdemoCount) + 1;
        }
        if (rowNumber % 100 < 98) {
            r.ws_bill_addr_sk = (rowNumber % addrCount) + 1;
        }

        // Ship-to customer
        if (rowNumber % 3 == 0) {
            r.ws_ship_customer_sk = r.ws_bill_customer_sk;
            r.ws_ship_cdemo_sk = r.ws_bill_cdemo_sk;
            r.ws_ship_hdemo_sk = r.ws_bill_hdemo_sk;
            r.ws_ship_addr_sk = r.ws_bill_addr_sk;
        } else {
            r.ws_ship_customer_sk = ((rowNumber + 1) % customerCount) + 1;
            if (rowNumber % 100 < 95) {
                r.ws_ship_cdemo_sk = ((rowNumber + 1) % cdemoCount) + 1;
            }
            if (rowNumber % 100 < 90) {
                r.ws_ship_hdemo_sk = ((rowNumber + 1) % hdemoCount) + 1;
            }
            if (rowNumber % 100 < 98) {
                r.ws_ship_addr_sk = ((rowNumber + 1) % addrCount) + 1;
            }
        }

        // Other foreign keys
        if (webPageCount > 0) {
            r.ws_web_page_sk = (rowNumber % webPageCount) + 1;
        }
        if (webSiteCount > 0) {
            r.ws_web_site_sk = (rowNumber % webSiteCount) + 1;
        }
        r.ws_ship_mode_sk = (rowNumber % shipModeCount) + 1;
        if (warehouseCount > 0) {
            r.ws_warehouse_sk = (rowNumber % warehouseCount) + 1;
        }
        if (rowNumber % 100 < 50) {
            r.ws_promo_sk = (rowNumber % promoCount) + 1;
        }

        r.ws_order_number = rowNumber / 10 + 1;
        r.ws_quantity = (int) ((rowNumber % 100) + 1);

        // Prices
        long wholesaleCost = 100 + (rowNumber % 9900);
        long listPrice = wholesaleCost + (rowNumber % 5000);
        long salesPrice = listPrice - (rowNumber % 1000);
        long shipCost = 200 + (rowNumber % 800);

        r.ws_wholesale_cost = new DsDecimal(wholesaleCost, 2, 5);
        r.ws_list_price = new DsDecimal(listPrice, 2, 5);
        r.ws_sales_price = new DsDecimal(salesPrice, 2, 5);

        // Extended amounts
        long extWholesale = wholesaleCost * r.ws_quantity;
        long extList = listPrice * r.ws_quantity;
        long extSales = salesPrice * r.ws_quantity;
        long extDiscount = extList - extSales;
        long tax = extSales / 10;
        long coupon = (rowNumber % 100 < 30) ? (extSales / 20) : 0;
        long extShip = shipCost * r.ws_quantity;

        r.ws_ext_wholesale_cost = new DsDecimal(extWholesale, 2, 7);
        r.ws_ext_list_price = new DsDecimal(extList, 2, 7);
        r.ws_ext_sales_price = new DsDecimal(extSales, 2, 7);
        r.ws_ext_discount_amt = new DsDecimal(extDiscount, 2, 7);
        r.ws_ext_tax = new DsDecimal(tax, 2, 7);
        r.ws_coupon_amt = new DsDecimal(coupon, 2, 7);
        r.ws_ext_ship_cost = new DsDecimal(extShip, 2, 7);

        // Net amounts
        long netPaid = extSales - coupon;
        long netPaidIncTax = netPaid + tax;
        long netPaidIncShip = netPaid + extShip;
        long netPaidIncShipTax = netPaidIncShip + tax;
        long netProfit = netPaid - extWholesale;

        r.ws_net_paid = new DsDecimal(netPaid, 2, 7);
        r.ws_net_paid_inc_tax = new DsDecimal(netPaidIncTax, 2, 7);
        r.ws_net_paid_inc_ship = new DsDecimal(netPaidIncShip, 2, 7);
        r.ws_net_paid_inc_ship_tax = new DsDecimal(netPaidIncShipTax, 2, 7);
        r.ws_net_profit = new DsDecimal(netProfit, 2, 7);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(WebSalesRow r, String delimiter) {
        return join(delimiter,
            r.ws_sold_date_sk,
            r.ws_sold_time_sk,
            r.ws_ship_date_sk,
            r.ws_item_sk,
            r.ws_bill_customer_sk,
            r.ws_bill_cdemo_sk,
            r.ws_bill_hdemo_sk,
            r.ws_bill_addr_sk,
            r.ws_ship_customer_sk,
            r.ws_ship_cdemo_sk,
            r.ws_ship_hdemo_sk,
            r.ws_ship_addr_sk,
            r.ws_web_page_sk,
            r.ws_web_site_sk,
            r.ws_ship_mode_sk,
            r.ws_warehouse_sk,
            r.ws_promo_sk,
            r.ws_order_number,
            r.ws_quantity,
            r.ws_wholesale_cost,
            r.ws_list_price,
            r.ws_sales_price,
            r.ws_ext_discount_amt,
            r.ws_ext_sales_price,
            r.ws_ext_wholesale_cost,
            r.ws_ext_list_price,
            r.ws_ext_tax,
            r.ws_coupon_amt,
            r.ws_ext_ship_cost,
            r.ws_net_paid,
            r.ws_net_paid_inc_tax,
            r.ws_net_paid_inc_ship,
            r.ws_net_paid_inc_ship_tax,
            r.ws_net_profit
        );
    }
}
