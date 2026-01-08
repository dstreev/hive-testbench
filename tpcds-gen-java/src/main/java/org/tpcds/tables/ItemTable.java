package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.ItemRow;
import org.tpcds.types.DsDecimal;
import org.tpcds.util.BuildSupport;
import org.tpcds.util.Scaling;

public class ItemTable extends BaseTable<ItemRow> {

    private static final String[] COLUMN_NAMES = {
        "i_item_sk", "i_item_id", "i_rec_start_date", "i_rec_end_date",
        "i_item_desc", "i_current_price", "i_wholesale_cost", "i_brand_id",
        "i_brand", "i_class_id", "i_class", "i_category_id", "i_category",
        "i_manufact_id", "i_manufact", "i_size", "i_formulation", "i_color",
        "i_units", "i_container", "i_manager_id", "i_product_name"
    };

    private final BuildSupport buildSupport;

    public ItemTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.ITEM, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.ITEM, scaleFactor);
    }

    private static final String[] BRANDS = {"Brand #1", "Brand #2", "Brand #3", "Brand #4", "Brand #5"};
    private static final String[] CLASSES = {"shirts", "pants", "dresses", "jewelry", "electronics", "books", "music", "toys"};
    private static final String[] CATEGORIES = {"Women", "Men", "Children", "Sports", "Home", "Books", "Electronics", "Music", "Jewelry", "Shoes"};
    private static final String[] MANUFACTURERS = {"Manufacturer #1", "Manufacturer #2", "Manufacturer #3", "Manufacturer #4", "Manufacturer #5"};
    private static final String[] SIZES = {"small", "medium", "large", "extra large", "petite", "economy", "N/A"};
    private static final String[] COLORS = {"red", "blue", "green", "yellow", "orange", "purple", "white", "black", "brown", "pink"};
    private static final String[] UNITS = {"Pallet", "Case", "Box", "Each", "Unknown", "Lb", "Oz", "Bundle", "Gross", "Dozen", "Tbl", "Cup"};
    private static final String[] CONTAINERS = {"Unknown", "Large", "Medium", "Small", "Wrap"};

    @Override
    public ItemRow buildRow(long rowNumber) {
        ItemRow r = new ItemRow();

        int stream = (int) (rowNumber % 100);

        r.i_item_sk = rowNumber;
        r.i_item_id = buildSupport.mkBkey(rowNumber);

        // Record dates
        r.i_rec_start_date_sk = 2450815;  // 1998-01-01
        r.i_rec_end_date_sk = null;  // Still active

        // Item description
        r.i_item_desc = buildSupport.mkWord("syllables", rowNumber, 200);

        // Prices - using DsDecimal (2 decimal places)
        long priceInCents = 50 + (rowNumber % 30000);
        r.i_current_price = new DsDecimal(priceInCents, 2, 5);
        r.i_wholesale_cost = new DsDecimal((priceInCents * 6) / 10, 2, 5);

        // Brand hierarchy - use hardcoded values
        r.i_brand_id = (rowNumber % 1000) + 1;
        r.i_brand = BRANDS[(int)(rowNumber % BRANDS.length)];

        // Class hierarchy
        r.i_class_id = (rowNumber % 100) + 1;
        r.i_class = CLASSES[(int)(rowNumber % CLASSES.length)];

        // Category hierarchy
        r.i_category_id = (rowNumber % 10) + 1;
        r.i_category = CATEGORIES[(int)(rowNumber % CATEGORIES.length)];

        // Manufacturer
        r.i_manufact_id = (rowNumber % 1000) + 1;
        r.i_manufact = MANUFACTURERS[(int)(rowNumber % MANUFACTURERS.length)];

        // Product attributes
        r.i_size = SIZES[(int)(rowNumber % SIZES.length)];
        r.i_formulation = String.format("%d%d%d%d%d%d",
            rowNumber % 10, (rowNumber / 10) % 10, (rowNumber / 100) % 10,
            (rowNumber / 1000) % 10, (rowNumber / 10000) % 10, (rowNumber / 100000) % 10);
        r.i_color = COLORS[(int)(rowNumber % COLORS.length)];
        r.i_units = UNITS[(int)(rowNumber % UNITS.length)];
        r.i_container = CONTAINERS[(int)(rowNumber % CONTAINERS.length)];

        // Manager
        r.i_manager_id = (rowNumber % 100) + 1;

        // Product name
        r.i_product_name = distMgr.pickDistribution("syllables", 1, 1, stream) +
            distMgr.pickDistribution("syllables", 1, 1, stream + 1);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(ItemRow r, String delimiter) {
        return join(delimiter,
            r.i_item_sk,
            r.i_item_id,
            r.i_rec_start_date_sk,
            r.i_rec_end_date_sk,
            r.i_item_desc,
            r.i_current_price,
            r.i_wholesale_cost,
            r.i_brand_id,
            r.i_brand,
            r.i_class_id,
            r.i_class,
            r.i_category_id,
            r.i_category,
            r.i_manufact_id,
            r.i_manufact,
            r.i_size,
            r.i_formulation,
            r.i_color,
            r.i_units,
            r.i_container,
            r.i_manager_id,
            r.i_product_name
        );
    }
}
