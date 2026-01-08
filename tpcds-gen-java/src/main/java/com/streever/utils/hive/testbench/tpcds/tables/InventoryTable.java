package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.InventoryRow;
import com.streever.utils.hive.testbench.tpcds.util.Scaling;

public class InventoryTable extends BaseTable<InventoryRow> {

    private static final String[] COLUMN_NAMES = {
        "inv_date_sk", "inv_item_sk", "inv_warehouse_sk", "inv_quantity_on_hand"
    };

    // Inventory is taken weekly over 2 years
    private static final int WEEKS = 52 * 2;
    private static final int START_DATE = 2450815;  // 1998-01-01

    public InventoryTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.INVENTORY, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        // inventory = items * warehouses * weeks
        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        long warehouseCount = Scaling.getRowCount(TableId.WAREHOUSE, scaleFactor);
        return itemCount * warehouseCount * WEEKS;
    }

    @Override
    public InventoryRow buildRow(long rowNumber) {
        InventoryRow r = new InventoryRow();

        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        long warehouseCount = Scaling.getRowCount(TableId.WAREHOUSE, scaleFactor);

        // Decompose row number into item, warehouse, and week
        long temp = rowNumber;
        long itemIdx = temp % itemCount;
        temp /= itemCount;
        long warehouseIdx = temp % warehouseCount;
        temp /= warehouseCount;
        long weekIdx = temp % WEEKS;

        r.inv_item_sk = itemIdx + 1;
        r.inv_warehouse_sk = warehouseIdx + 1;
        r.inv_date_sk = START_DATE + (weekIdx * 7);

        // Random quantity between 0 and 1000
        r.inv_quantity_on_hand = (int) (rowNumber % 1001);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(InventoryRow r, String delimiter) {
        return join(delimiter,
            r.inv_date_sk,
            r.inv_item_sk,
            r.inv_warehouse_sk,
            r.inv_quantity_on_hand
        );
    }
}
