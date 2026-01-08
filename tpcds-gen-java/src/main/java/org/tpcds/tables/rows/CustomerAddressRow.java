package org.tpcds.tables.rows;

import org.tpcds.util.Address;

public class CustomerAddressRow {
    public long ca_address_sk;
    public String ca_address_id;
    public Address ca_address;
    public String ca_location_type;
}
