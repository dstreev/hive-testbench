/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.DateDimRow;
import com.streever.utils.hive.testbench.tpcds.types.DsDate;

/**
 * Generator for the date_dim dimension table.
 * Contains one row for each day in the range 1900-01-02 to 2100-01-01 (73049 rows).
 */
public class DateDimTable extends BaseTable<DateDimRow> {

    private static final long ROW_COUNT = 73049L;  // Days from 1900-01-02 to 2100-01-01

    // Constants from the C code
    private static final int CURRENT_DAY = 2451545;    // Julian day for 2000-01-01
    private static final int CURRENT_YEAR = 2000;
    private static final int CURRENT_MONTH = 1;
    private static final int CURRENT_QUARTER = 1;
    private static final int CURRENT_WEEK = 1;

    private static final DsDate BASE_DATE = DsDate.fromString("1900-01-01");

    private static final String[] COLUMN_NAMES = {
        "d_date_sk", "d_date_id", "d_date", "d_month_seq", "d_week_seq", "d_quarter_seq",
        "d_year", "d_dow", "d_moy", "d_dom", "d_qoy", "d_fy_year", "d_fy_quarter_seq",
        "d_fy_week_seq", "d_day_name", "d_quarter_name", "d_holiday", "d_weekend",
        "d_following_holiday", "d_first_dom", "d_last_dom", "d_same_day_ly", "d_same_day_lq",
        "d_current_day", "d_current_week", "d_current_month", "d_current_quarter", "d_current_year"
    };

    public DateDimTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.DATE_DIM, rng, distMgr, scaleFactor);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return ROW_COUNT;
    }

    @Override
    public DateDimRow buildRow(long rowNumber) {
        DateDimRow r = new DateDimRow();

        int julian = (int) rowNumber + BASE_DATE.toJulian();
        DsDate date = DsDate.fromJulian(julian);

        r.d_date_sk = julian;
        r.d_date_id = generateBusinessKey("AAAAAAAAAAAA", julian, 0);
        r.d_date = date.toString();

        r.d_year = date.getYear();
        r.d_dow = date.dayOfWeek();
        r.d_moy = date.getMonth();
        r.d_dom = date.getDay();

        // Sequence calculations
        r.d_week_seq = ((int) rowNumber + 6) / 7;
        r.d_month_seq = (r.d_year - 1900) * 12 + r.d_moy - 1;
        r.d_quarter_seq = (r.d_year - 1900) * 4 + r.d_moy / 3 + 1;

        // Get quarter of year from calendar distribution
        int dayIndex = date.dayNumber();
        r.d_qoy = Integer.parseInt(distMgr.distMember("calendar", dayIndex, 6));

        // Fiscal year = calendar year
        r.d_fy_year = r.d_year;
        r.d_fy_quarter_seq = r.d_quarter_seq;
        r.d_fy_week_seq = r.d_week_seq;

        r.d_day_name = DsDate.WEEKDAY_NAMES[r.d_dow + 1];
        r.d_quarter_name = String.format("%dQ%d", r.d_year, r.d_qoy);

        // Holiday and weekend flags
        r.d_holiday = Integer.parseInt(distMgr.distMember("calendar", dayIndex, 8));
        r.d_weekend = (r.d_dow == 5 || r.d_dow == 6) ? 1 : 0;

        // Following holiday
        if (dayIndex == 1) {
            r.d_following_holiday = Integer.parseInt(distMgr.distMember("calendar",
                365 + (DsDate.isLeap(r.d_year - 1) ? 1 : 0), 8));
        } else {
            r.d_following_holiday = Integer.parseInt(distMgr.distMember("calendar", dayIndex - 1, 8));
        }

        // Date operations
        DsDate firstDom = date.operate(DsDate.OP_FIRST_DOM, null);
        DsDate lastDom = date.operate(DsDate.OP_LAST_DOM, null);
        DsDate sameDayLy = date.operate(DsDate.OP_SAME_LY, null);
        DsDate sameDayLq = date.operate(DsDate.OP_SAME_LQ, null);

        r.d_first_dom = firstDom.toJulian();
        r.d_last_dom = lastDom.toJulian();
        r.d_same_day_ly = sameDayLy.toJulian();
        r.d_same_day_lq = sameDayLq.toJulian();

        // Current flags
        r.d_current_day = (r.d_date_sk == CURRENT_DAY) ? 1 : 0;
        r.d_current_year = (r.d_year == CURRENT_YEAR) ? 1 : 0;
        if (r.d_current_year == 1) {
            r.d_current_month = (r.d_moy == CURRENT_MONTH) ? 1 : 0;
            r.d_current_quarter = (r.d_qoy == CURRENT_QUARTER) ? 1 : 0;
            r.d_current_week = (r.d_week_seq == CURRENT_WEEK) ? 1 : 0;
        }

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(DateDimRow r, String delimiter) {
        return join(delimiter,
            r.d_date_sk,
            r.d_date_id,
            r.d_date,
            r.d_month_seq,
            r.d_week_seq,
            r.d_quarter_seq,
            r.d_year,
            r.d_dow,
            r.d_moy,
            r.d_dom,
            r.d_qoy,
            r.d_fy_year,
            r.d_fy_quarter_seq,
            r.d_fy_week_seq,
            r.d_day_name,
            r.d_quarter_name,
            r.d_holiday == 1 ? "Y" : "N",
            r.d_weekend == 1 ? "Y" : "N",
            r.d_following_holiday == 1 ? "Y" : "N",
            r.d_first_dom,
            r.d_last_dom,
            r.d_same_day_ly,
            r.d_same_day_lq,
            r.d_current_day == 1 ? "Y" : "N",
            r.d_current_week == 1 ? "Y" : "N",
            r.d_current_month == 1 ? "Y" : "N",
            r.d_current_quarter == 1 ? "Y" : "N",
            r.d_current_year == 1 ? "Y" : "N"
        );
    }
}
