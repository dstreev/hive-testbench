package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.HouseholdDemographicsRow;
import com.streever.utils.hive.testbench.tpcds.util.BuildSupport;

public class HouseholdDemographicsTable extends BaseTable<HouseholdDemographicsRow> {

    // Fixed row count: income_band(20) * buy_potential(6) * dep_count(10) * vehicle_count(6)
    private static final long ROW_COUNT = 7200L;
    private static final String[] COLUMN_NAMES = {
        "hd_demo_sk", "hd_income_band_sk", "hd_buy_potential", "hd_dep_count", "hd_vehicle_count"
    };

    private final BuildSupport buildSupport;

    public HouseholdDemographicsTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.HOUSEHOLD_DEMOGRAPHICS, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return ROW_COUNT;  // Fixed regardless of scale
    }

    @Override
    public HouseholdDemographicsRow buildRow(long rowNumber) {
        HouseholdDemographicsRow r = new HouseholdDemographicsRow();
        long[] temp = {rowNumber};

        r.hd_demo_sk = rowNumber;

        // Income band (1-20)
        int incomeBandSize = distMgr.distSize("income_band");
        r.hd_income_band_sk = (temp[0] % incomeBandSize) + 1;
        temp[0] /= incomeBandSize;

        r.hd_buy_potential = buildSupport.bitmapToDist("buy_potential", temp, 1);
        r.hd_dep_count = (int) (temp[0] % 10);
        temp[0] /= 10;
        r.hd_vehicle_count = (int) (temp[0] % 6);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(HouseholdDemographicsRow r, String delimiter) {
        return join(delimiter,
            r.hd_demo_sk,
            r.hd_income_band_sk,
            r.hd_buy_potential,
            r.hd_dep_count,
            r.hd_vehicle_count
        );
    }
}
