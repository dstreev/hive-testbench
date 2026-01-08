package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.CallCenterRow;
import org.tpcds.types.DsDecimal;
import org.tpcds.util.Address;
import org.tpcds.util.BuildSupport;
import org.tpcds.util.Scaling;

public class CallCenterTable extends BaseTable<CallCenterRow> {

    private static final String[] CALL_CENTER_CLASSES = {"small", "medium", "large"};

    private static final String[] COLUMN_NAMES = {
        "cc_call_center_sk", "cc_call_center_id", "cc_rec_start_date", "cc_rec_end_date",
        "cc_closed_date_sk", "cc_open_date_sk", "cc_name", "cc_class", "cc_employees",
        "cc_sq_ft", "cc_hours", "cc_manager", "cc_mkt_id", "cc_mkt_class", "cc_mkt_desc",
        "cc_market_manager", "cc_division", "cc_division_name", "cc_company",
        "cc_company_name", "cc_street_number", "cc_street_name", "cc_street_type",
        "cc_suite_number", "cc_city", "cc_county", "cc_state", "cc_zip", "cc_country",
        "cc_gmt_offset", "cc_tax_percentage"
    };

    private final BuildSupport buildSupport;

    public CallCenterTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.CALL_CENTER, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.CALL_CENTER, scaleFactor);
    }

    @Override
    public CallCenterRow buildRow(long rowNumber) {
        CallCenterRow r = new CallCenterRow();

        int stream = (int) (rowNumber % 100);  // Bound stream to avoid overflow

        r.cc_call_center_sk = rowNumber;
        r.cc_call_center_id = buildSupport.mkBkey(rowNumber);

        // Record dates
        r.cc_rec_start_date_sk = 2450815;  // 1998-01-01
        r.cc_rec_end_date_sk = null;

        // Open/close dates
        r.cc_open_date_sk = 2450815L + (rowNumber % 365);
        if (rowNumber % 10 == 0) {
            r.cc_closed_date_sk = r.cc_open_date_sk + 1000;
        }

        r.cc_name = "Call Center " + rowNumber;
        r.cc_class = CALL_CENTER_CLASSES[(int)(rowNumber % CALL_CENTER_CLASSES.length)];

        r.cc_employees = (int) (10 + (rowNumber % 50));
        r.cc_sq_ft = (int) (1000 + (rowNumber % 9000));

        r.cc_hours = distMgr.pickDistribution("hours", 1, 1, stream);

        String mgrFirst = distMgr.pickDistribution("first_names", 1, 1, stream + 1);
        String mgrLast = distMgr.pickDistribution("last_names", 1, 1, stream + 2);
        r.cc_manager = mgrFirst + " " + mgrLast;

        r.cc_mkt_id = (int) ((rowNumber % 6) + 1);
        r.cc_mkt_class = CALL_CENTER_CLASSES[(int)((rowNumber * 3) % CALL_CENTER_CLASSES.length)];
        r.cc_mkt_desc = buildSupport.mkWord("syllables", rowNumber, 100);

        String mmFirst = distMgr.pickDistribution("first_names", 1, 1, stream + 3);
        String mmLast = distMgr.pickDistribution("last_names", 1, 1, stream + 4);
        r.cc_market_manager = mmFirst + " " + mmLast;

        r.cc_division = (rowNumber % 5) + 1;
        r.cc_division_name = "Division " + r.cc_division;

        r.cc_company = (rowNumber % 3) + 1;
        r.cc_company_name = "Company " + r.cc_company;

        r.cc_address = buildSupport.mkAddress((int) rowNumber);

        r.cc_tax_percentage = new DsDecimal((rowNumber % 12), 2, 2);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(CallCenterRow r, String delimiter) {
        Address a = r.cc_address;
        return join(delimiter,
            r.cc_call_center_sk,
            r.cc_call_center_id,
            r.cc_rec_start_date_sk,
            r.cc_rec_end_date_sk,
            r.cc_closed_date_sk,
            r.cc_open_date_sk,
            r.cc_name,
            r.cc_class,
            r.cc_employees,
            r.cc_sq_ft,
            r.cc_hours,
            r.cc_manager,
            r.cc_mkt_id,
            r.cc_mkt_class,
            r.cc_mkt_desc,
            r.cc_market_manager,
            r.cc_division,
            r.cc_division_name,
            r.cc_company,
            r.cc_company_name,
            a.streetNum,
            a.getFullStreetName(),
            a.streetType,
            a.suiteNum,
            a.city,
            a.county,
            a.state,
            a.getFormattedZip(),
            a.country,
            a.gmtOffset,
            r.cc_tax_percentage
        );
    }
}
