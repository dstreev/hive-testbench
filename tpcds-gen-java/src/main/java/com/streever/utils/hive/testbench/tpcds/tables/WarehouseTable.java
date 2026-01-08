package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.WarehouseRow;
import com.streever.utils.hive.testbench.tpcds.util.BuildSupport;

public class WarehouseTable extends BaseTable<WarehouseRow> {

    private static final int W_SQFT_MIN = 50000;
    private static final int W_SQFT_MAX = 1000000;
    private static final int W_NAME_MIN = 10;
    private static final int W_NAME_MAX = 20;
    private static final String[] COLUMN_NAMES = {
        "w_warehouse_sk", "w_warehouse_id", "w_warehouse_name", "w_warehouse_sq_ft",
        "w_street_number", "w_street_name", "w_street_type", "w_suite_number",
        "w_city", "w_county", "w_state", "w_zip", "w_country", "w_gmt_offset"
    };

    private final BuildSupport buildSupport;

    public WarehouseTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.WAREHOUSE, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Math.max(5, 5 * scaleFactor / 10);
    }

    @Override
    public WarehouseRow buildRow(long rowNumber) {
        WarehouseRow r = new WarehouseRow();

        r.w_warehouse_sk = rowNumber;
        r.w_warehouse_id = buildSupport.mkBkey(rowNumber);
        r.w_warehouse_name = buildSupport.genText(W_NAME_MIN, W_NAME_MAX, (int) rowNumber);
        r.w_warehouse_sq_ft = rng.genrandInteger(RandomNumberGenerator.DIST_UNIFORM,
            W_SQFT_MIN, W_SQFT_MAX, 0, (int) rowNumber);
        r.w_address = buildSupport.mkAddress((int) rowNumber);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(WarehouseRow r, String delimiter) {
        return join(delimiter,
            r.w_warehouse_sk,
            r.w_warehouse_id,
            r.w_warehouse_name,
            r.w_warehouse_sq_ft,
            r.w_address.streetNum,
            r.w_address.getFullStreetName(),
            r.w_address.streetType,
            r.w_address.suiteNum,
            r.w_address.city,
            r.w_address.county,
            r.w_address.state,
            r.w_address.getFormattedZip(),
            r.w_address.country,
            r.w_address.gmtOffset
        );
    }
}
