package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.CatalogPageRow;
import org.tpcds.util.BuildSupport;
import org.tpcds.util.Scaling;

public class CatalogPageTable extends BaseTable<CatalogPageRow> {

    private static final String[] DEPARTMENTS = {"DEPARTMENT1", "DEPARTMENT2", "DEPARTMENT3", "DEPARTMENT4", "DEPARTMENT5"};
    private static final String[] PAGE_TYPES = {"bi-annual", "quarterly", "monthly"};

    private static final String[] COLUMN_NAMES = {
        "cp_catalog_page_sk", "cp_catalog_page_id", "cp_start_date_sk", "cp_end_date_sk",
        "cp_department", "cp_catalog_number", "cp_catalog_page_number", "cp_description", "cp_type"
    };

    private final BuildSupport buildSupport;

    public CatalogPageTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.CATALOG_PAGE, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.CATALOG_PAGE, scaleFactor);
    }

    @Override
    public CatalogPageRow buildRow(long rowNumber) {
        CatalogPageRow r = new CatalogPageRow();

        r.cp_catalog_page_sk = rowNumber;
        r.cp_catalog_page_id = buildSupport.mkBkey(rowNumber);

        // Date range
        r.cp_start_date_sk = 2450815L + (rowNumber % 365);
        r.cp_end_date_sk = r.cp_start_date_sk + 30 + (rowNumber % 335);

        r.cp_department = DEPARTMENTS[(int)(rowNumber % DEPARTMENTS.length)];

        // Catalog and page numbers
        r.cp_catalog_number = (int) ((rowNumber / 100) + 1);
        r.cp_catalog_page_number = (int) ((rowNumber % 100) + 1);

        r.cp_description = buildSupport.mkWord("syllables", rowNumber, 100);
        r.cp_type = PAGE_TYPES[(int)(rowNumber % PAGE_TYPES.length)];

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(CatalogPageRow r, String delimiter) {
        return join(delimiter,
            r.cp_catalog_page_sk,
            r.cp_catalog_page_id,
            r.cp_start_date_sk,
            r.cp_end_date_sk,
            r.cp_department,
            r.cp_catalog_number,
            r.cp_catalog_page_number,
            r.cp_description,
            r.cp_type
        );
    }
}
