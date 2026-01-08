/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.ReasonRow;

/**
 * Generator for the reason dimension table.
 * Contains one row for each return reason in the distribution.
 */
public class ReasonTable extends BaseTable<ReasonRow> {

    private static final String[] COLUMN_NAMES = {
        "r_reason_sk", "r_reason_id", "r_reason_desc"
    };

    public ReasonTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.REASON, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        // Row count matches the size of the "return_reasons" distribution
        return distMgr.distSize("return_reasons");
    }

    @Override
    public ReasonRow buildRow(long rowNumber) {
        ReasonRow r = new ReasonRow();

        r.r_reason_sk = rowNumber;
        r.r_reason_id = generateBusinessKey("AAAAAAAAAAAA", rowNumber, 0);
        r.r_reason_description = distMgr.distMember("return_reasons", (int) rowNumber, 1);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(ReasonRow r, String delimiter) {
        return join(delimiter,
            r.r_reason_sk,
            r.r_reason_id,
            r.r_reason_description
        );
    }
}
