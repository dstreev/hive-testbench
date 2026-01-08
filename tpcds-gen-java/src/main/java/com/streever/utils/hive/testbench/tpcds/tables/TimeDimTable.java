/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.TimeDimRow;

/**
 * Generator for the time_dim dimension table.
 * Contains one row for each second in a day (86400 rows).
 */
public class TimeDimTable extends BaseTable<TimeDimRow> {

    private static final long ROW_COUNT = 86400L;  // Seconds in a day

    private static final String[] COLUMN_NAMES = {
        "t_time_sk", "t_time_id", "t_time", "t_hour", "t_minute", "t_second",
        "t_am_pm", "t_shift", "t_sub_shift", "t_meal_time"
    };

    public TimeDimTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.TIME_DIM, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return ROW_COUNT;
    }

    @Override
    public TimeDimRow buildRow(long rowNumber) {
        TimeDimRow r = new TimeDimRow();

        // t_time_sk is 0-based
        r.t_time_sk = rowNumber - 1;
        r.t_time_id = generateBusinessKey("AAAAAAAAAAAAAAAA", rowNumber, 0);
        r.t_time = (int) (rowNumber - 1);

        // Calculate hour, minute, second from seconds since midnight
        int temp = (int) (rowNumber - 1);
        r.t_second = temp % 60;
        temp /= 60;
        r.t_minute = temp % 60;
        temp /= 60;
        r.t_hour = temp % 24;

        // Get AM/PM, shift, sub_shift, meal_time from "hours" distribution
        // Distribution index is 1-based, hour+1 gives us 1-24
        r.t_am_pm = distMgr.distMember("hours", r.t_hour + 1, 2);
        r.t_shift = distMgr.distMember("hours", r.t_hour + 1, 3);
        r.t_sub_shift = distMgr.distMember("hours", r.t_hour + 1, 4);
        r.t_meal_time = distMgr.distMember("hours", r.t_hour + 1, 5);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(TimeDimRow r, String delimiter) {
        return join(delimiter,
            r.t_time_sk,
            r.t_time_id,
            r.t_time,
            r.t_hour,
            r.t_minute,
            r.t_second,
            r.t_am_pm,
            r.t_shift,
            r.t_sub_shift,
            r.t_meal_time
        );
    }
}
