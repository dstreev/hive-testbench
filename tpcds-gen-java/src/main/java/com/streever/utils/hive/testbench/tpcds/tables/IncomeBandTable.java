package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.IncomeBandRow;

public class IncomeBandTable extends BaseTable<IncomeBandRow> {

    private static final String[] COLUMN_NAMES = {
        "ib_income_band_sk", "ib_lower_bound", "ib_upper_bound"
    };

    public IncomeBandTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.INCOME_BAND, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return distMgr.distSize("income_band");
    }

    @Override
    public IncomeBandRow buildRow(long rowNumber) {
        IncomeBandRow r = new IncomeBandRow();

        r.ib_income_band_sk = rowNumber;
        r.ib_lower_bound = Integer.parseInt(distMgr.distMember("income_band", (int) rowNumber, 1));
        r.ib_upper_bound = Integer.parseInt(distMgr.distMember("income_band", (int) rowNumber, 2));

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(IncomeBandRow r, String delimiter) {
        return join(delimiter,
            r.ib_income_band_sk,
            r.ib_lower_bound,
            r.ib_upper_bound
        );
    }
}
