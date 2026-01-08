package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.CustomerDemographicsRow;
import com.streever.utils.hive.testbench.tpcds.util.BuildSupport;

public class CustomerDemographicsTable extends BaseTable<CustomerDemographicsRow> {

    // Fixed row count: gender(2) * marital(5) * education(8) * purchase_est(20) * credit(4) * deps(7) * emp_deps(7) * college_deps(7)
    private static final long ROW_COUNT = 1920800L;
    private static final String[] COLUMN_NAMES = {
        "cd_demo_sk", "cd_gender", "cd_marital_status", "cd_education_status",
        "cd_purchase_estimate", "cd_credit_rating", "cd_dep_count",
        "cd_dep_employed_count", "cd_dep_college_count"
    };

    private final BuildSupport buildSupport;

    public CustomerDemographicsTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.CUSTOMER_DEMOGRAPHICS, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return ROW_COUNT;  // Fixed regardless of scale
    }

    @Override
    public CustomerDemographicsRow buildRow(long rowNumber) {
        CustomerDemographicsRow r = new CustomerDemographicsRow();
        long[] temp = {rowNumber};

        r.cd_demo_sk = rowNumber;
        r.cd_gender = buildSupport.bitmapToDist("gender", temp, 1);
        r.cd_marital_status = buildSupport.bitmapToDist("marital_status", temp, 1);
        r.cd_education_status = buildSupport.bitmapToDist("education", temp, 1);
        r.cd_purchase_estimate = Integer.parseInt(buildSupport.bitmapToDist("purchase_band", temp, 1));
        r.cd_credit_rating = buildSupport.bitmapToDist("credit_rating", temp, 1);
        r.cd_dep_count = (int) (temp[0] % 7);
        temp[0] /= 7;
        r.cd_dep_employed_count = (int) (temp[0] % 7);
        temp[0] /= 7;
        r.cd_dep_college_count = (int) (temp[0] % 7);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(CustomerDemographicsRow r, String delimiter) {
        return join(delimiter,
            r.cd_demo_sk,
            r.cd_gender,
            r.cd_marital_status,
            r.cd_education_status,
            r.cd_purchase_estimate,
            r.cd_credit_rating,
            r.cd_dep_count,
            r.cd_dep_employed_count,
            r.cd_dep_college_count
        );
    }
}
