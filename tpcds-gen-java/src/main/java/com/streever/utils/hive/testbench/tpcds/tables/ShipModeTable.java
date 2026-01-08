package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.ShipModeRow;
import com.streever.utils.hive.testbench.tpcds.util.BuildSupport;

public class ShipModeTable extends BaseTable<ShipModeRow> {

    private static final long ROW_COUNT = 20L;
    private static final String[] COLUMN_NAMES = {
        "sm_ship_mode_sk", "sm_ship_mode_id", "sm_type", "sm_code", "sm_carrier", "sm_contract"
    };

    private final BuildSupport buildSupport;

    public ShipModeTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.SHIP_MODE, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return ROW_COUNT;
    }

    @Override
    public ShipModeRow buildRow(long rowNumber) {
        ShipModeRow r = new ShipModeRow();
        long[] temp = {rowNumber};

        r.sm_ship_mode_sk = rowNumber;
        r.sm_ship_mode_id = buildSupport.mkBkey(rowNumber);
        r.sm_type = buildSupport.bitmapToDist("ship_mode_type", temp, 1);
        r.sm_code = buildSupport.bitmapToDist("ship_mode_code", temp, 1);
        r.sm_carrier = distMgr.distMember("ship_mode_carrier", (int) rowNumber, 1);
        r.sm_contract = rng.genCharset(RandomNumberGenerator.ALPHANUM, 1, 20, (int) rowNumber);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(ShipModeRow r, String delimiter) {
        return join(delimiter,
            r.sm_ship_mode_sk,
            r.sm_ship_mode_id,
            r.sm_type,
            r.sm_code,
            r.sm_carrier,
            r.sm_contract
        );
    }
}
