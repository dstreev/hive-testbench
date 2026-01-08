package org.tpcds.tables.rows;

import org.tpcds.types.DsDecimal;

public class ItemRow {
    public long i_item_sk;
    public String i_item_id;
    public Integer i_rec_start_date_sk;
    public Integer i_rec_end_date_sk;
    public String i_item_desc;
    public DsDecimal i_current_price;
    public DsDecimal i_wholesale_cost;
    public Long i_brand_id;
    public String i_brand;
    public Long i_class_id;
    public String i_class;
    public Long i_category_id;
    public String i_category;
    public Long i_manufact_id;
    public String i_manufact;
    public String i_size;
    public String i_formulation;
    public String i_color;
    public String i_units;
    public String i_container;
    public Long i_manager_id;
    public String i_product_name;
}
