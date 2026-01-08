package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;
import com.streever.utils.hive.testbench.tpcds.tables.rows.CustomerRow;
import com.streever.utils.hive.testbench.tpcds.util.BuildSupport;
import com.streever.utils.hive.testbench.tpcds.util.Scaling;

public class CustomerTable extends BaseTable<CustomerRow> {

    private static final String[] COLUMN_NAMES = {
        "c_customer_sk", "c_customer_id", "c_current_cdemo_sk", "c_current_hdemo_sk",
        "c_current_addr_sk", "c_first_shipto_date_sk", "c_first_sales_date_sk",
        "c_salutation", "c_first_name", "c_last_name", "c_preferred_cust_flag",
        "c_birth_day", "c_birth_month", "c_birth_year", "c_birth_country",
        "c_login", "c_email_address", "c_last_review_date_sk"
    };

    // Date ranges (Julian days)
    private static final int CUSTOMER_MIN_DATE = 2415021;  // 1900-01-01
    private static final int CUSTOMER_MAX_DATE = 2488070;  // 2100-01-01

    private final BuildSupport buildSupport;

    public CustomerTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.CUSTOMER, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return Scaling.getRowCount(TableId.CUSTOMER, scaleFactor);
    }

    @Override
    public CustomerRow buildRow(long rowNumber) {
        CustomerRow r = new CustomerRow();

        // Use modulo for stream to avoid array overflow
        int stream = (int) (rowNumber % 100);

        r.c_customer_sk = rowNumber;
        r.c_customer_id = buildSupport.mkBkey(rowNumber);

        // Demographics - can be null with some probability
        long cdemoCount = 1920800L;  // customer_demographics row count
        long hdemoCount = 7200L;     // household_demographics row count
        long addrCount = 50000L * scaleFactor;

        // Use row number to deterministically assign demographics
        if (rowNumber % 100 < 95) {  // 95% have demographics
            r.c_current_cdemo_sk = (rowNumber % cdemoCount) + 1;
        }
        if (rowNumber % 100 < 90) {  // 90% have household demographics
            r.c_current_hdemo_sk = (rowNumber % hdemoCount) + 1;
        }
        if (rowNumber % 100 < 98) {  // 98% have address
            r.c_current_addr_sk = (rowNumber % addrCount) + 1;
        }

        // Date fields
        int dateRange = CUSTOMER_MAX_DATE - CUSTOMER_MIN_DATE;
        r.c_first_shipto_date_sk = (int) (CUSTOMER_MIN_DATE + (rowNumber % dateRange));
        r.c_first_sales_date_sk = r.c_first_shipto_date_sk + (int) (rowNumber % 30);

        // Name fields from distributions - use modulo for stream
        r.c_salutation = distMgr.pickDistribution("salutations", 1, 1, stream);
        r.c_first_name = distMgr.pickDistribution("first_names", 1, 1, stream + 1);
        r.c_last_name = distMgr.pickDistribution("last_names", 1, 1, stream + 2);

        // Preferred customer flag
        r.c_preferred_cust_flag = (rowNumber % 2 == 0) ? "Y" : "N";

        // Birth date
        r.c_birth_day = (int) ((rowNumber % 28) + 1);
        r.c_birth_month = (int) ((rowNumber % 12) + 1);
        r.c_birth_year = (int) (1930 + (rowNumber % 70));

        r.c_birth_country = distMgr.pickDistribution("countries", 1, 1, stream + 3);

        // Login is typically empty
        r.c_login = "";

        // Email address
        String firstName = r.c_first_name != null ? r.c_first_name.toLowerCase().replace(" ", "") : "user";
        String lastName = r.c_last_name != null ? r.c_last_name.toLowerCase().replace(" ", "") : "customer";
        r.c_email_address = firstName + "." + lastName + "@" +
            distMgr.pickDistribution("top_domains", 1, 1, stream + 4);

        // Last review date
        r.c_last_review_date_sk = r.c_first_sales_date_sk + (int) (rowNumber % 365);

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(CustomerRow r, String delimiter) {
        return join(delimiter,
            r.c_customer_sk,
            r.c_customer_id,
            r.c_current_cdemo_sk,
            r.c_current_hdemo_sk,
            r.c_current_addr_sk,
            r.c_first_shipto_date_sk,
            r.c_first_sales_date_sk,
            r.c_salutation,
            r.c_first_name,
            r.c_last_name,
            r.c_preferred_cust_flag,
            r.c_birth_day,
            r.c_birth_month,
            r.c_birth_year,
            r.c_birth_country,
            r.c_login,
            r.c_email_address,
            r.c_last_review_date_sk
        );
    }
}
