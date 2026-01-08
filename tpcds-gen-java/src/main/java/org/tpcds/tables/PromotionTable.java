package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.PromotionRow;
import org.tpcds.types.DsDecimal;
import org.tpcds.util.BuildSupport;
import org.tpcds.util.Scaling;

public class PromotionTable extends BaseTable<PromotionRow> {

    private static final String[] COLUMN_NAMES = {
        "p_promo_sk", "p_promo_id", "p_start_date_sk", "p_end_date_sk",
        "p_item_sk", "p_cost", "p_response_target", "p_promo_name",
        "p_channel_dmail", "p_channel_email", "p_channel_catalog",
        "p_channel_tv", "p_channel_radio", "p_channel_press",
        "p_channel_event", "p_channel_demo", "p_channel_details",
        "p_purpose", "p_discount_active"
    };

    private final BuildSupport buildSupport;

    public PromotionTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.PROMOTION, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.PROMOTION, scaleFactor);
    }

    @Override
    public PromotionRow buildRow(long rowNumber) {
        PromotionRow r = new PromotionRow();

        r.p_promo_sk = rowNumber;
        r.p_promo_id = buildSupport.mkBkey(rowNumber);

        // Promotion dates
        r.p_start_date_sk = 2450815L + (rowNumber % 365);
        r.p_end_date_sk = r.p_start_date_sk + 7 + (rowNumber % 21);  // 1-4 weeks

        // Item (can be null - not all promotions are item-specific)
        long itemCount = Scaling.getRowCount(TableId.ITEM, scaleFactor);
        if (rowNumber % 3 != 0) {
            r.p_item_sk = (rowNumber % itemCount) + 1;
        }

        // Cost and response - using DsDecimal (2 decimal places)
        long costInCents = 100000 + (rowNumber % 9900000);  // $1,000 to $100,000
        r.p_cost = new DsDecimal(costInCents, 2, 7);
        r.p_response_target = (int) (1000 + (rowNumber % 9000));

        // Promotion name
        r.p_promo_name = "Promotion " + rowNumber;

        // Channel flags - use bit pattern from row number
        long bits = rowNumber;
        r.p_channel_dmail = (bits & 1) != 0 ? "Y" : "N";
        r.p_channel_email = (bits & 2) != 0 ? "Y" : "N";
        r.p_channel_catalog = (bits & 4) != 0 ? "Y" : "N";
        r.p_channel_tv = (bits & 8) != 0 ? "Y" : "N";
        r.p_channel_radio = (bits & 16) != 0 ? "Y" : "N";
        r.p_channel_press = (bits & 32) != 0 ? "Y" : "N";
        r.p_channel_event = (bits & 64) != 0 ? "Y" : "N";
        r.p_channel_demo = (bits & 128) != 0 ? "Y" : "N";

        // Channel details
        r.p_channel_details = buildSupport.mkWord("syllables", rowNumber * 7, 100);

        // Purpose
        r.p_purpose = distMgr.pickDistribution("promo_purpose", 1, 1, (int) rowNumber);

        // Discount active
        r.p_discount_active = (rowNumber % 2 == 0) ? "Y" : "N";

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(PromotionRow r, String delimiter) {
        return join(delimiter,
            r.p_promo_sk,
            r.p_promo_id,
            r.p_start_date_sk,
            r.p_end_date_sk,
            r.p_item_sk,
            r.p_cost,
            r.p_response_target,
            r.p_promo_name,
            r.p_channel_dmail,
            r.p_channel_email,
            r.p_channel_catalog,
            r.p_channel_tv,
            r.p_channel_radio,
            r.p_channel_press,
            r.p_channel_event,
            r.p_channel_demo,
            r.p_channel_details,
            r.p_purpose,
            r.p_discount_active
        );
    }
}
