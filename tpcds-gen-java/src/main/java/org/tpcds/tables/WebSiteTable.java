package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.WebSiteRow;
import org.tpcds.types.DsDecimal;
import org.tpcds.util.Address;
import org.tpcds.util.BuildSupport;
import org.tpcds.util.Scaling;

public class WebSiteTable extends BaseTable<WebSiteRow> {

    private static final String[] WEB_CLASSES = {"Unknown", "Large", "Medium", "Small"};

    private static final String[] COLUMN_NAMES = {
        "web_site_sk", "web_site_id", "web_rec_start_date", "web_rec_end_date",
        "web_name", "web_open_date_sk", "web_close_date_sk", "web_class", "web_manager",
        "web_mkt_id", "web_mkt_class", "web_mkt_desc", "web_market_manager",
        "web_company_id", "web_company_name", "web_street_number", "web_street_name",
        "web_street_type", "web_suite_number", "web_city", "web_county", "web_state",
        "web_zip", "web_country", "web_gmt_offset", "web_tax_percentage"
    };

    private final BuildSupport buildSupport;

    public WebSiteTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.WEB_SITE, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.WEB_SITE, scaleFactor);
    }

    @Override
    public WebSiteRow buildRow(long rowNumber) {
        WebSiteRow r = new WebSiteRow();

        int stream = (int) (rowNumber % 100);  // Bound stream to avoid overflow

        r.web_site_sk = rowNumber;
        r.web_site_id = buildSupport.mkBkey(rowNumber);

        // Record dates
        r.web_rec_start_date_sk = 2450815;  // 1998-01-01
        r.web_rec_end_date_sk = null;

        r.web_name = "Site " + rowNumber;

        // Open/close dates
        r.web_open_date_sk = 2450815L + (rowNumber % 365);
        if (rowNumber % 10 == 0) {
            r.web_close_date_sk = r.web_open_date_sk + 1000;
        }

        r.web_class = WEB_CLASSES[(int)(rowNumber % WEB_CLASSES.length)];

        String mgrFirst = distMgr.pickDistribution("first_names", 1, 1, stream);
        String mgrLast = distMgr.pickDistribution("last_names", 1, 1, stream + 1);
        r.web_manager = mgrFirst + " " + mgrLast;

        r.web_mkt_id = (int) ((rowNumber % 6) + 1);
        r.web_mkt_class = WEB_CLASSES[(int)((rowNumber * 3) % WEB_CLASSES.length)];
        r.web_mkt_desc = buildSupport.mkWord("syllables", rowNumber, 100);

        String mmFirst = distMgr.pickDistribution("first_names", 1, 1, stream + 2);
        String mmLast = distMgr.pickDistribution("last_names", 1, 1, stream + 3);
        r.web_market_manager = mmFirst + " " + mmLast;

        r.web_company_id = (rowNumber % 3) + 1;
        r.web_company_name = "Company " + r.web_company_id;

        r.web_address = buildSupport.mkAddress((int) rowNumber);

        r.web_tax_percentage = new DsDecimal((rowNumber % 12), 2, 2);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(WebSiteRow r, String delimiter) {
        Address a = r.web_address;
        return join(delimiter,
            r.web_site_sk,
            r.web_site_id,
            r.web_rec_start_date_sk,
            r.web_rec_end_date_sk,
            r.web_name,
            r.web_open_date_sk,
            r.web_close_date_sk,
            r.web_class,
            r.web_manager,
            r.web_mkt_id,
            r.web_mkt_class,
            r.web_mkt_desc,
            r.web_market_manager,
            r.web_company_id,
            r.web_company_name,
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
            r.web_tax_percentage
        );
    }
}
