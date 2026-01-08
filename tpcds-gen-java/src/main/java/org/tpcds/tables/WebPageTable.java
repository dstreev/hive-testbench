package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.WebPageRow;
import org.tpcds.util.BuildSupport;
import org.tpcds.util.Scaling;

public class WebPageTable extends BaseTable<WebPageRow> {

    private static final String[] COLUMN_NAMES = {
        "wp_web_page_sk", "wp_web_page_id", "wp_rec_start_date", "wp_rec_end_date",
        "wp_creation_date_sk", "wp_access_date_sk", "wp_autogen_flag", "wp_customer_sk",
        "wp_url", "wp_type", "wp_char_count", "wp_link_count", "wp_image_count", "wp_max_ad_count"
    };

    private final BuildSupport buildSupport;

    public WebPageTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.WEB_PAGE, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.WEB_PAGE, scaleFactor);
    }

    @Override
    public WebPageRow buildRow(long rowNumber) {
        WebPageRow r = new WebPageRow();

        r.wp_web_page_sk = rowNumber;
        r.wp_web_page_id = buildSupport.mkBkey(rowNumber);

        // Record dates
        r.wp_rec_start_date_sk = 2450815;  // 1998-01-01
        r.wp_rec_end_date_sk = null;

        // Creation and access dates
        r.wp_creation_date_sk = 2450815L + (rowNumber % 365);
        r.wp_access_date_sk = r.wp_creation_date_sk + (rowNumber % 100);

        r.wp_autogen_flag = (rowNumber % 2 == 0) ? "Y" : "N";

        // Customer who created page (can be null)
        long customerCount = Scaling.getRowCount(TableId.CUSTOMER, scaleFactor);
        if (rowNumber % 5 != 0) {
            r.wp_customer_sk = (rowNumber % customerCount) + 1;
        }

        r.wp_url = "http://www.foo.com/page" + rowNumber;
        r.wp_type = distMgr.pickDistribution("web_page_type", 1, 1, (int) rowNumber);

        r.wp_char_count = (int) (100 + (rowNumber % 9900));
        r.wp_link_count = (int) (rowNumber % 25);
        r.wp_image_count = (int) (rowNumber % 10);
        r.wp_max_ad_count = (int) ((rowNumber % 5) + 1);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(WebPageRow r, String delimiter) {
        return join(delimiter,
            r.wp_web_page_sk,
            r.wp_web_page_id,
            r.wp_rec_start_date_sk,
            r.wp_rec_end_date_sk,
            r.wp_creation_date_sk,
            r.wp_access_date_sk,
            r.wp_autogen_flag,
            r.wp_customer_sk,
            r.wp_url,
            r.wp_type,
            r.wp_char_count,
            r.wp_link_count,
            r.wp_image_count,
            r.wp_max_ad_count
        );
    }
}
