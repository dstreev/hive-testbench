package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;
import org.tpcds.tables.rows.CustomerAddressRow;
import org.tpcds.util.BuildSupport;

public class CustomerAddressTable extends BaseTable<CustomerAddressRow> {

    private static final String[] LOCATION_TYPES = {"single family", "condo", "apartment"};

    private static final String[] COLUMN_NAMES = {
        "ca_address_sk", "ca_address_id", "ca_street_number", "ca_street_name",
        "ca_street_type", "ca_suite_number", "ca_city", "ca_county", "ca_state",
        "ca_zip", "ca_country", "ca_gmt_offset", "ca_location_type"
    };

    private final BuildSupport buildSupport;

    public CustomerAddressTable(RandomNumberGenerator rng, DistributionManager distMgr, int scaleFactor) {
        super(TableId.CUSTOMER_ADDRESS, rng, distMgr, scaleFactor);
        this.buildSupport = new BuildSupport(rng, distMgr);
    }

    @Override
    public long getRowCount(int scaleFactor) {
        return 50000L * scaleFactor;
    }

    @Override
    public CustomerAddressRow buildRow(long rowNumber) {
        CustomerAddressRow r = new CustomerAddressRow();

        r.ca_address_sk = rowNumber;
        r.ca_address_id = buildSupport.mkBkey(rowNumber);
        r.ca_address = buildSupport.mkAddress((int) (rowNumber % Integer.MAX_VALUE));
        r.ca_location_type = LOCATION_TYPES[(int)(rowNumber % LOCATION_TYPES.length)];

        return r;
    }

    @Override
    public String[] getColumnNames() {
        return COLUMN_NAMES;
    }

    @Override
    public String formatRow(CustomerAddressRow r, String delimiter) {
        return join(delimiter,
            r.ca_address_sk,
            r.ca_address_id,
            r.ca_address.streetNum,
            r.ca_address.getFullStreetName(),
            r.ca_address.streetType,
            r.ca_address.suiteNum,
            r.ca_address.city,
            r.ca_address.county,
            r.ca_address.state,
            r.ca_address.getFormattedZip(),
            r.ca_address.country,
            r.ca_address.gmtOffset,
            r.ca_location_type
        );
    }
}
