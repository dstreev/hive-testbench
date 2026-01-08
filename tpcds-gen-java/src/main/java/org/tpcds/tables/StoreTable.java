package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.StoreRow;
import org.tpcds.types.DsDecimal;
import org.tpcds.util.Address;
import org.tpcds.util.BuildSupport;
import org.tpcds.util.Scaling;

public class StoreTable extends BaseTable<StoreRow> {

    private static final String[] GEOGRAPHY_CLASSES = {"Unknown", "Urban", "Suburban", "Rural"};

    private static final String[] COLUMN_NAMES = {
        "s_store_sk", "s_store_id", "s_rec_start_date", "s_rec_end_date",
        "s_closed_date_sk", "s_store_name", "s_number_employees", "s_floor_space",
        "s_hours", "s_manager", "s_market_id", "s_geography_class",
        "s_market_desc", "s_market_manager", "s_division_id", "s_division_name",
        "s_company_id", "s_company_name", "s_street_number", "s_street_name",
        "s_street_type", "s_suite_number", "s_city", "s_county", "s_state",
        "s_zip", "s_country", "s_gmt_offset", "s_tax_percentage"
    };

    private final BuildSupport buildSupport;

    public StoreTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.STORE, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.STORE, scaleFactor);
    }

    @Override
    public StoreRow buildRow(long rowNumber) {
        StoreRow r = new StoreRow();

        int stream = (int) (rowNumber % 100);  // Bound stream to avoid overflow

        r.s_store_sk = rowNumber;
        r.s_store_id = buildSupport.mkBkey(rowNumber);

        // Record dates
        r.s_rec_start_date_sk = 2450815;  // 1998-01-01 in Julian
        r.s_rec_end_date_sk = null;  // Still active

        // Closed date - most stores open
        if (rowNumber % 20 == 0) {
            r.s_closed_date_sk = 2451545L;  // 2000-01-01
        }

        // Store name
        r.s_store_name = "Store #" + rowNumber;

        // Employees and floor space
        r.s_number_employees = (int) (50 + (rowNumber % 200));
        r.s_floor_space = (int) (5000 + (rowNumber % 95000));

        // Hours
        r.s_hours = distMgr.pickDistribution("hours", 1, 1, stream);

        // Manager name
        String mgrFirst = distMgr.pickDistribution("first_names", 1, 1, stream + 1);
        String mgrLast = distMgr.pickDistribution("last_names", 1, 1, stream + 2);
        r.s_manager = mgrFirst + " " + mgrLast;

        // Market info
        r.s_market_id = (int) ((rowNumber % 10) + 1);
        r.s_geography_class = GEOGRAPHY_CLASSES[(int)(rowNumber % GEOGRAPHY_CLASSES.length)];
        r.s_market_desc = buildSupport.mkWord("syllables", rowNumber, 100);

        String mmFirst = distMgr.pickDistribution("first_names", 1, 1, stream + 3);
        String mmLast = distMgr.pickDistribution("last_names", 1, 1, stream + 4);
        r.s_market_manager = mmFirst + " " + mmLast;

        // Division info
        r.s_division_id = ((rowNumber % 5) + 1);
        r.s_division_name = "Division " + r.s_division_id;

        // Company info
        r.s_company_id = ((rowNumber % 3) + 1);
        r.s_company_name = "Company " + r.s_company_id;

        // Address
        r.s_address = buildSupport.mkAddress((int) rowNumber);

        // Tax percentage (0-11%)
        r.s_tax_percentage = new DsDecimal((rowNumber % 12), 2, 2);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(StoreRow r, String delimiter) {
        Address a = r.s_address;
        return join(delimiter,
            r.s_store_sk,
            r.s_store_id,
            r.s_rec_start_date_sk,
            r.s_rec_end_date_sk,
            r.s_closed_date_sk,
            r.s_store_name,
            r.s_number_employees,
            r.s_floor_space,
            r.s_hours,
            r.s_manager,
            r.s_market_id,
            r.s_geography_class,
            r.s_market_desc,
            r.s_market_manager,
            r.s_division_id,
            r.s_division_name,
            r.s_company_id,
            r.s_company_name,
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
            r.s_tax_percentage
        );
    }
}
