package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;

/**
 * Factory for creating TPC-DS table generators.
 */
public class TableFactory {

    /**
     * Create a table generator for the specified table.
     *
     * @param tableId The table identifier
     * @param rng The random number generator
     * @param distMgr The distribution manager
     * @param scaleFactor The scale factor
     * @return The table generator, or null if not implemented
     */
    public static Table<?> createTable(TableId tableId, RandomNumberGenerator rng,
                                       DistributionManager distMgr, int scaleFactor) {
        switch (tableId) {
            case CALL_CENTER:
                return new CallCenterTable(rng, distMgr, scaleFactor);
            case CATALOG_PAGE:
                return new CatalogPageTable(rng, distMgr, scaleFactor);
            case CATALOG_RETURNS:
                return new CatalogReturnsTable(rng, distMgr, scaleFactor);
            case CATALOG_SALES:
                return new CatalogSalesTable(rng, distMgr, scaleFactor);
            case CUSTOMER:
                return new CustomerTable(rng, distMgr, scaleFactor);
            case CUSTOMER_ADDRESS:
                return new CustomerAddressTable(rng, distMgr, scaleFactor);
            case CUSTOMER_DEMOGRAPHICS:
                return new CustomerDemographicsTable(rng, distMgr, scaleFactor);
            case DATE_DIM:
                return new DateDimTable(rng, distMgr, scaleFactor);
            case HOUSEHOLD_DEMOGRAPHICS:
                return new HouseholdDemographicsTable(rng, distMgr, scaleFactor);
            case INCOME_BAND:
                return new IncomeBandTable(rng, distMgr, scaleFactor);
            case INVENTORY:
                return new InventoryTable(rng, distMgr, scaleFactor);
            case ITEM:
                return new ItemTable(rng, distMgr, scaleFactor);
            case PROMOTION:
                return new PromotionTable(rng, distMgr, scaleFactor);
            case REASON:
                return new ReasonTable(rng, distMgr, scaleFactor);
            case SHIP_MODE:
                return new ShipModeTable(rng, distMgr, scaleFactor);
            case STORE:
                return new StoreTable(rng, distMgr, scaleFactor);
            case STORE_RETURNS:
                return new StoreReturnsTable(rng, distMgr, scaleFactor);
            case STORE_SALES:
                return new StoreSalesTable(rng, distMgr, scaleFactor);
            case TIME_DIM:
                return new TimeDimTable(rng, distMgr, scaleFactor);
            case WAREHOUSE:
                return new WarehouseTable(rng, distMgr, scaleFactor);
            case WEB_PAGE:
                return new WebPageTable(rng, distMgr, scaleFactor);
            case WEB_RETURNS:
                return new WebReturnsTable(rng, distMgr, scaleFactor);
            case WEB_SALES:
                return new WebSalesTable(rng, distMgr, scaleFactor);
            case WEB_SITE:
                return new WebSiteTable(rng, distMgr, scaleFactor);
            default:
                return null;
        }
    }
}
