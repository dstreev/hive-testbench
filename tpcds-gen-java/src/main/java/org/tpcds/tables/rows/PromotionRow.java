package org.tpcds.tables.rows;

import org.tpcds.types.DsDecimal;

public class PromotionRow {
    public long p_promo_sk;
    public String p_promo_id;
    public Long p_start_date_sk;
    public Long p_end_date_sk;
    public Long p_item_sk;
    public DsDecimal p_cost;
    public int p_response_target;
    public String p_promo_name;
    public String p_channel_dmail;
    public String p_channel_email;
    public String p_channel_catalog;
    public String p_channel_tv;
    public String p_channel_radio;
    public String p_channel_press;
    public String p_channel_event;
    public String p_channel_demo;
    public String p_channel_details;
    public String p_purpose;
    public String p_discount_active;
}
