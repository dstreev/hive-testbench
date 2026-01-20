-- ============================================
-- TPC-DS Table and Column Comments
-- ============================================
-- Purpose: Add descriptive comments to TPC-DS tables and columns
--          to improve agentic SQL generation capabilities
--
-- Usage: Run after table creation with DB variable set
--   hive --hivevar DB=tpcds_text_100 -f add_comments.sql
--   hive --hivevar DB=tpcds_bin_partitioned_orc_100 -f add_comments.sql
--
-- Source: TPC-DS Specification v4.0.0 (tpc.org)
-- ============================================

USE ${DB};

-- ============================================
-- DIMENSION TABLES
-- ============================================

-- --------------------------------------------
-- CUSTOMER
-- --------------------------------------------
ALTER TABLE customer SET TBLPROPERTIES ('comment' = 'Dimension table containing customer profile information including demographics, contact details, and account history');

ALTER TABLE customer CHANGE COLUMN c_customer_sk c_customer_sk bigint COMMENT 'Surrogate key uniquely identifying each customer';
ALTER TABLE customer CHANGE COLUMN c_customer_id c_customer_id string COMMENT 'Natural key - business identifier for the customer';
ALTER TABLE customer CHANGE COLUMN c_current_cdemo_sk c_current_cdemo_sk bigint COMMENT 'FK to customer_demographics - current demographic profile';
ALTER TABLE customer CHANGE COLUMN c_current_hdemo_sk c_current_hdemo_sk bigint COMMENT 'FK to household_demographics - current household profile';
ALTER TABLE customer CHANGE COLUMN c_current_addr_sk c_current_addr_sk bigint COMMENT 'FK to customer_address - current address';
ALTER TABLE customer CHANGE COLUMN c_first_shipto_date_sk c_first_shipto_date_sk bigint COMMENT 'FK to date_dim - date of first shipment to customer';
ALTER TABLE customer CHANGE COLUMN c_first_sales_date_sk c_first_sales_date_sk bigint COMMENT 'FK to date_dim - date of first sale to customer';
ALTER TABLE customer CHANGE COLUMN c_salutation c_salutation string COMMENT 'Customer salutation (Mr., Mrs., Ms., etc.)';
ALTER TABLE customer CHANGE COLUMN c_first_name c_first_name string COMMENT 'Customer first name';
ALTER TABLE customer CHANGE COLUMN c_last_name c_last_name string COMMENT 'Customer last name';
ALTER TABLE customer CHANGE COLUMN c_preferred_cust_flag c_preferred_cust_flag string COMMENT 'Y/N flag indicating preferred customer status';
ALTER TABLE customer CHANGE COLUMN c_birth_day c_birth_day int COMMENT 'Day of month customer was born (1-31)';
ALTER TABLE customer CHANGE COLUMN c_birth_month c_birth_month int COMMENT 'Month customer was born (1-12)';
ALTER TABLE customer CHANGE COLUMN c_birth_year c_birth_year int COMMENT 'Year customer was born';
ALTER TABLE customer CHANGE COLUMN c_birth_country c_birth_country string COMMENT 'Country where customer was born';
ALTER TABLE customer CHANGE COLUMN c_login c_login string COMMENT 'Customer login username';
ALTER TABLE customer CHANGE COLUMN c_email_address c_email_address string COMMENT 'Customer email address';
ALTER TABLE customer CHANGE COLUMN c_last_review_date_sk c_last_review_date_sk string COMMENT 'Date of last customer review';

-- --------------------------------------------
-- CUSTOMER_ADDRESS
-- --------------------------------------------
ALTER TABLE customer_address SET TBLPROPERTIES ('comment' = 'Dimension table containing customer address information - each customer can have multiple addresses');

ALTER TABLE customer_address CHANGE COLUMN ca_address_sk ca_address_sk bigint COMMENT 'Surrogate key uniquely identifying each address';
ALTER TABLE customer_address CHANGE COLUMN ca_address_id ca_address_id string COMMENT 'Natural key - business identifier for the address';
ALTER TABLE customer_address CHANGE COLUMN ca_street_number ca_street_number string COMMENT 'Street number portion of address';
ALTER TABLE customer_address CHANGE COLUMN ca_street_name ca_street_name string COMMENT 'Street name portion of address';
ALTER TABLE customer_address CHANGE COLUMN ca_street_type ca_street_type string COMMENT 'Street type (Ave, Blvd, St, etc.)';
ALTER TABLE customer_address CHANGE COLUMN ca_suite_number ca_suite_number string COMMENT 'Suite or apartment number';
ALTER TABLE customer_address CHANGE COLUMN ca_city ca_city string COMMENT 'City name';
ALTER TABLE customer_address CHANGE COLUMN ca_county ca_county string COMMENT 'County name';
ALTER TABLE customer_address CHANGE COLUMN ca_state ca_state string COMMENT 'State abbreviation (2 characters)';
ALTER TABLE customer_address CHANGE COLUMN ca_zip ca_zip string COMMENT 'ZIP/postal code';
ALTER TABLE customer_address CHANGE COLUMN ca_country ca_country string COMMENT 'Country name';
ALTER TABLE customer_address CHANGE COLUMN ca_gmt_offset ca_gmt_offset decimal(5,2) COMMENT 'GMT timezone offset for this location';
ALTER TABLE customer_address CHANGE COLUMN ca_location_type ca_location_type string COMMENT 'Type of location (single family, apartment, condo, etc.)';

-- --------------------------------------------
-- CUSTOMER_DEMOGRAPHICS
-- --------------------------------------------
ALTER TABLE customer_demographics SET TBLPROPERTIES ('comment' = 'Dimension table containing unique combinations of customer demographic attributes for segmentation analysis');

ALTER TABLE customer_demographics CHANGE COLUMN cd_demo_sk cd_demo_sk bigint COMMENT 'Surrogate key uniquely identifying each demographic combination';
ALTER TABLE customer_demographics CHANGE COLUMN cd_gender cd_gender string COMMENT 'Gender (M=Male, F=Female)';
ALTER TABLE customer_demographics CHANGE COLUMN cd_marital_status cd_marital_status string COMMENT 'Marital status (S=Single, M=Married, D=Divorced, W=Widowed, U=Unknown)';
ALTER TABLE customer_demographics CHANGE COLUMN cd_education_status cd_education_status string COMMENT 'Highest education level attained';
ALTER TABLE customer_demographics CHANGE COLUMN cd_purchase_estimate cd_purchase_estimate int COMMENT 'Estimated annual purchase amount in dollars';
ALTER TABLE customer_demographics CHANGE COLUMN cd_credit_rating cd_credit_rating string COMMENT 'Credit rating category (High Risk, Low Risk, Good, Unknown)';
ALTER TABLE customer_demographics CHANGE COLUMN cd_dep_count cd_dep_count int COMMENT 'Number of dependents';
ALTER TABLE customer_demographics CHANGE COLUMN cd_dep_employed_count cd_dep_employed_count int COMMENT 'Number of employed dependents';
ALTER TABLE customer_demographics CHANGE COLUMN cd_dep_college_count cd_dep_college_count int COMMENT 'Number of dependents in college';

-- --------------------------------------------
-- DATE_DIM
-- --------------------------------------------
ALTER TABLE date_dim SET TBLPROPERTIES ('comment' = 'Dimension table containing one row per calendar day with extensive date attributes for temporal analysis');

ALTER TABLE date_dim CHANGE COLUMN d_date_sk d_date_sk bigint COMMENT 'Surrogate key derived from julian date';
ALTER TABLE date_dim CHANGE COLUMN d_date_id d_date_id string COMMENT 'Natural key - date identifier string';
ALTER TABLE date_dim CHANGE COLUMN d_date d_date string COMMENT 'Actual calendar date';
ALTER TABLE date_dim CHANGE COLUMN d_month_seq d_month_seq int COMMENT 'Sequential month number from a fixed start point';
ALTER TABLE date_dim CHANGE COLUMN d_week_seq d_week_seq int COMMENT 'Sequential week number from a fixed start point';
ALTER TABLE date_dim CHANGE COLUMN d_quarter_seq d_quarter_seq int COMMENT 'Sequential quarter number from a fixed start point';
ALTER TABLE date_dim CHANGE COLUMN d_year d_year int COMMENT 'Calendar year (e.g., 2023)';
ALTER TABLE date_dim CHANGE COLUMN d_dow d_dow int COMMENT 'Day of week (0=Sunday through 6=Saturday)';
ALTER TABLE date_dim CHANGE COLUMN d_moy d_moy int COMMENT 'Month of year (1-12)';
ALTER TABLE date_dim CHANGE COLUMN d_dom d_dom int COMMENT 'Day of month (1-31)';
ALTER TABLE date_dim CHANGE COLUMN d_qoy d_qoy int COMMENT 'Quarter of year (1-4)';
ALTER TABLE date_dim CHANGE COLUMN d_fy_year d_fy_year int COMMENT 'Fiscal year';
ALTER TABLE date_dim CHANGE COLUMN d_fy_quarter_seq d_fy_quarter_seq int COMMENT 'Sequential fiscal quarter number';
ALTER TABLE date_dim CHANGE COLUMN d_fy_week_seq d_fy_week_seq int COMMENT 'Sequential fiscal week number';
ALTER TABLE date_dim CHANGE COLUMN d_day_name d_day_name string COMMENT 'Day name (Sunday, Monday, etc.)';
ALTER TABLE date_dim CHANGE COLUMN d_quarter_name d_quarter_name string COMMENT 'Quarter name (e.g., 2023Q1)';
ALTER TABLE date_dim CHANGE COLUMN d_holiday d_holiday string COMMENT 'Y/N flag indicating if date is a holiday';
ALTER TABLE date_dim CHANGE COLUMN d_weekend d_weekend string COMMENT 'Y/N flag indicating if date is a weekend';
ALTER TABLE date_dim CHANGE COLUMN d_following_holiday d_following_holiday string COMMENT 'Y/N flag indicating if date follows a holiday';
ALTER TABLE date_dim CHANGE COLUMN d_first_dom d_first_dom int COMMENT 'Date key of first day of this month';
ALTER TABLE date_dim CHANGE COLUMN d_last_dom d_last_dom int COMMENT 'Date key of last day of this month';
ALTER TABLE date_dim CHANGE COLUMN d_same_day_ly d_same_day_ly int COMMENT 'Date key of same day last year';
ALTER TABLE date_dim CHANGE COLUMN d_same_day_lq d_same_day_lq int COMMENT 'Date key of same day last quarter';
ALTER TABLE date_dim CHANGE COLUMN d_current_day d_current_day string COMMENT 'Y/N flag indicating if this is the current day';
ALTER TABLE date_dim CHANGE COLUMN d_current_week d_current_week string COMMENT 'Y/N flag indicating if this is the current week';
ALTER TABLE date_dim CHANGE COLUMN d_current_month d_current_month string COMMENT 'Y/N flag indicating if this is the current month';
ALTER TABLE date_dim CHANGE COLUMN d_current_quarter d_current_quarter string COMMENT 'Y/N flag indicating if this is the current quarter';
ALTER TABLE date_dim CHANGE COLUMN d_current_year d_current_year string COMMENT 'Y/N flag indicating if this is the current year';

-- --------------------------------------------
-- TIME_DIM
-- --------------------------------------------
ALTER TABLE time_dim SET TBLPROPERTIES ('comment' = 'Dimension table containing time-of-day attributes for intraday analysis');

ALTER TABLE time_dim CHANGE COLUMN t_time_sk t_time_sk bigint COMMENT 'Surrogate key uniquely identifying each second of the day';
ALTER TABLE time_dim CHANGE COLUMN t_time_id t_time_id string COMMENT 'Natural key - time identifier string';
ALTER TABLE time_dim CHANGE COLUMN t_time t_time int COMMENT 'Seconds since midnight (0-86399)';
ALTER TABLE time_dim CHANGE COLUMN t_hour t_hour int COMMENT 'Hour of day (0-23)';
ALTER TABLE time_dim CHANGE COLUMN t_minute t_minute int COMMENT 'Minute of hour (0-59)';
ALTER TABLE time_dim CHANGE COLUMN t_second t_second int COMMENT 'Second of minute (0-59)';
ALTER TABLE time_dim CHANGE COLUMN t_am_pm t_am_pm string COMMENT 'AM or PM indicator';
ALTER TABLE time_dim CHANGE COLUMN t_shift t_shift string COMMENT 'Work shift name (first, second, third)';
ALTER TABLE time_dim CHANGE COLUMN t_sub_shift t_sub_shift string COMMENT 'Sub-shift period (morning, afternoon, evening, night)';
ALTER TABLE time_dim CHANGE COLUMN t_meal_time t_meal_time string COMMENT 'Meal time indicator (breakfast, lunch, dinner, or null)';

-- --------------------------------------------
-- ITEM
-- --------------------------------------------
ALTER TABLE item SET TBLPROPERTIES ('comment' = 'Dimension table containing product catalog with attributes like brand, category, size, color, and pricing');

ALTER TABLE item CHANGE COLUMN i_item_sk i_item_sk bigint COMMENT 'Surrogate key uniquely identifying each product formulation';
ALTER TABLE item CHANGE COLUMN i_item_id i_item_id string COMMENT 'Natural key - business identifier for the item';
ALTER TABLE item CHANGE COLUMN i_rec_start_date i_rec_start_date string COMMENT 'SCD Type 2 - date this version became active';
ALTER TABLE item CHANGE COLUMN i_rec_end_date i_rec_end_date string COMMENT 'SCD Type 2 - date this version was superseded (null if current)';
ALTER TABLE item CHANGE COLUMN i_item_desc i_item_desc string COMMENT 'Full text description of the item';
ALTER TABLE item CHANGE COLUMN i_current_price i_current_price decimal(7,2) COMMENT 'Current retail price in dollars';
ALTER TABLE item CHANGE COLUMN i_wholesale_cost i_wholesale_cost decimal(7,2) COMMENT 'Wholesale cost in dollars';
ALTER TABLE item CHANGE COLUMN i_brand_id i_brand_id int COMMENT 'Brand identifier code';
ALTER TABLE item CHANGE COLUMN i_brand i_brand string COMMENT 'Brand name';
ALTER TABLE item CHANGE COLUMN i_class_id i_class_id int COMMENT 'Product class identifier code';
ALTER TABLE item CHANGE COLUMN i_class i_class string COMMENT 'Product class name';
ALTER TABLE item CHANGE COLUMN i_category_id i_category_id int COMMENT 'Product category identifier code';
ALTER TABLE item CHANGE COLUMN i_category i_category string COMMENT 'Product category name (e.g., Electronics, Apparel)';
ALTER TABLE item CHANGE COLUMN i_manufact_id i_manufact_id int COMMENT 'Manufacturer identifier code';
ALTER TABLE item CHANGE COLUMN i_manufact i_manufact string COMMENT 'Manufacturer name';
ALTER TABLE item CHANGE COLUMN i_size i_size string COMMENT 'Product size (small, medium, large, etc.)';
ALTER TABLE item CHANGE COLUMN i_formulation i_formulation string COMMENT 'Product formulation or variant code';
ALTER TABLE item CHANGE COLUMN i_color i_color string COMMENT 'Product color';
ALTER TABLE item CHANGE COLUMN i_units i_units string COMMENT 'Unit of measure (Each, Box, Case, etc.)';
ALTER TABLE item CHANGE COLUMN i_container i_container string COMMENT 'Container type (Unknown, Wrap, etc.)';
ALTER TABLE item CHANGE COLUMN i_manager_id i_manager_id int COMMENT 'Product manager identifier';
ALTER TABLE item CHANGE COLUMN i_product_name i_product_name string COMMENT 'Product marketing name';

-- --------------------------------------------
-- STORE
-- --------------------------------------------
ALTER TABLE store SET TBLPROPERTIES ('comment' = 'Dimension table containing physical retail store locations with attributes like geography, management, and market info');

ALTER TABLE store CHANGE COLUMN s_store_sk s_store_sk bigint COMMENT 'Surrogate key uniquely identifying each store';
ALTER TABLE store CHANGE COLUMN s_store_id s_store_id string COMMENT 'Natural key - business identifier for the store';
ALTER TABLE store CHANGE COLUMN s_rec_start_date s_rec_start_date string COMMENT 'SCD Type 2 - date this version became active';
ALTER TABLE store CHANGE COLUMN s_rec_end_date s_rec_end_date string COMMENT 'SCD Type 2 - date this version was superseded (null if current)';
ALTER TABLE store CHANGE COLUMN s_closed_date_sk s_closed_date_sk bigint COMMENT 'FK to date_dim - date store was closed (null if open)';
ALTER TABLE store CHANGE COLUMN s_store_name s_store_name string COMMENT 'Store name';
ALTER TABLE store CHANGE COLUMN s_number_employees s_number_employees int COMMENT 'Number of employees at this store';
ALTER TABLE store CHANGE COLUMN s_floor_space s_floor_space int COMMENT 'Total floor space in square feet';
ALTER TABLE store CHANGE COLUMN s_hours s_hours string COMMENT 'Store operating hours';
ALTER TABLE store CHANGE COLUMN s_manager s_manager string COMMENT 'Store manager name';
ALTER TABLE store CHANGE COLUMN s_market_id s_market_id int COMMENT 'Market identifier code';
ALTER TABLE store CHANGE COLUMN s_geography_class s_geography_class string COMMENT 'Geographic classification';
ALTER TABLE store CHANGE COLUMN s_market_desc s_market_desc string COMMENT 'Market description';
ALTER TABLE store CHANGE COLUMN s_market_manager s_market_manager string COMMENT 'Market manager name';
ALTER TABLE store CHANGE COLUMN s_division_id s_division_id int COMMENT 'Division identifier code';
ALTER TABLE store CHANGE COLUMN s_division_name s_division_name string COMMENT 'Division name';
ALTER TABLE store CHANGE COLUMN s_company_id s_company_id int COMMENT 'Company identifier code';
ALTER TABLE store CHANGE COLUMN s_company_name s_company_name string COMMENT 'Company name';
ALTER TABLE store CHANGE COLUMN s_street_number s_street_number string COMMENT 'Street number of store address';
ALTER TABLE store CHANGE COLUMN s_street_name s_street_name string COMMENT 'Street name of store address';
ALTER TABLE store CHANGE COLUMN s_street_type s_street_type string COMMENT 'Street type (Ave, Blvd, St, etc.)';
ALTER TABLE store CHANGE COLUMN s_suite_number s_suite_number string COMMENT 'Suite number of store address';
ALTER TABLE store CHANGE COLUMN s_city s_city string COMMENT 'City where store is located';
ALTER TABLE store CHANGE COLUMN s_county s_county string COMMENT 'County where store is located';
ALTER TABLE store CHANGE COLUMN s_state s_state string COMMENT 'State where store is located (2-char abbreviation)';
ALTER TABLE store CHANGE COLUMN s_zip s_zip string COMMENT 'ZIP code of store';
ALTER TABLE store CHANGE COLUMN s_country s_country string COMMENT 'Country where store is located';
ALTER TABLE store CHANGE COLUMN s_gmt_offset s_gmt_offset decimal(5,2) COMMENT 'GMT timezone offset for store location';
ALTER TABLE store CHANGE COLUMN s_tax_percentage s_tax_percentage decimal(5,2) COMMENT 'Sales tax percentage for store location';

-- --------------------------------------------
-- WAREHOUSE
-- --------------------------------------------
ALTER TABLE warehouse SET TBLPROPERTIES ('comment' = 'Dimension table containing distribution and storage facility information');

ALTER TABLE warehouse CHANGE COLUMN w_warehouse_sk w_warehouse_sk bigint COMMENT 'Surrogate key uniquely identifying each warehouse';
ALTER TABLE warehouse CHANGE COLUMN w_warehouse_id w_warehouse_id string COMMENT 'Natural key - business identifier for the warehouse';
ALTER TABLE warehouse CHANGE COLUMN w_warehouse_name w_warehouse_name string COMMENT 'Warehouse name';
ALTER TABLE warehouse CHANGE COLUMN w_warehouse_sq_ft w_warehouse_sq_ft int COMMENT 'Warehouse size in square feet';
ALTER TABLE warehouse CHANGE COLUMN w_street_number w_street_number string COMMENT 'Street number of warehouse address';
ALTER TABLE warehouse CHANGE COLUMN w_street_name w_street_name string COMMENT 'Street name of warehouse address';
ALTER TABLE warehouse CHANGE COLUMN w_street_type w_street_type string COMMENT 'Street type (Ave, Blvd, St, etc.)';
ALTER TABLE warehouse CHANGE COLUMN w_suite_number w_suite_number string COMMENT 'Suite number of warehouse address';
ALTER TABLE warehouse CHANGE COLUMN w_city w_city string COMMENT 'City where warehouse is located';
ALTER TABLE warehouse CHANGE COLUMN w_county w_county string COMMENT 'County where warehouse is located';
ALTER TABLE warehouse CHANGE COLUMN w_state w_state string COMMENT 'State where warehouse is located (2-char abbreviation)';
ALTER TABLE warehouse CHANGE COLUMN w_zip w_zip string COMMENT 'ZIP code of warehouse';
ALTER TABLE warehouse CHANGE COLUMN w_country w_country string COMMENT 'Country where warehouse is located';
ALTER TABLE warehouse CHANGE COLUMN w_gmt_offset w_gmt_offset decimal(5,2) COMMENT 'GMT timezone offset for warehouse location';

-- --------------------------------------------
-- CALL_CENTER
-- --------------------------------------------
ALTER TABLE call_center SET TBLPROPERTIES ('comment' = 'Dimension table containing customer service call center information for catalog sales support');

ALTER TABLE call_center CHANGE COLUMN cc_call_center_sk cc_call_center_sk bigint COMMENT 'Surrogate key uniquely identifying each call center';
ALTER TABLE call_center CHANGE COLUMN cc_call_center_id cc_call_center_id string COMMENT 'Natural key - business identifier for the call center';
ALTER TABLE call_center CHANGE COLUMN cc_rec_start_date cc_rec_start_date string COMMENT 'SCD Type 2 - date this version became active';
ALTER TABLE call_center CHANGE COLUMN cc_rec_end_date cc_rec_end_date string COMMENT 'SCD Type 2 - date this version was superseded (null if current)';
ALTER TABLE call_center CHANGE COLUMN cc_closed_date_sk cc_closed_date_sk bigint COMMENT 'FK to date_dim - date call center was closed (null if open)';
ALTER TABLE call_center CHANGE COLUMN cc_open_date_sk cc_open_date_sk bigint COMMENT 'FK to date_dim - date call center opened';
ALTER TABLE call_center CHANGE COLUMN cc_name cc_name string COMMENT 'Call center name';
ALTER TABLE call_center CHANGE COLUMN cc_class cc_class string COMMENT 'Call center classification (small, medium, large)';
ALTER TABLE call_center CHANGE COLUMN cc_employees cc_employees int COMMENT 'Number of employees at this call center';
ALTER TABLE call_center CHANGE COLUMN cc_sq_ft cc_sq_ft int COMMENT 'Call center size in square feet';
ALTER TABLE call_center CHANGE COLUMN cc_hours cc_hours string COMMENT 'Operating hours';
ALTER TABLE call_center CHANGE COLUMN cc_manager cc_manager string COMMENT 'Call center manager name';
ALTER TABLE call_center CHANGE COLUMN cc_mkt_id cc_mkt_id int COMMENT 'Market identifier code';
ALTER TABLE call_center CHANGE COLUMN cc_mkt_class cc_mkt_class string COMMENT 'Market classification';
ALTER TABLE call_center CHANGE COLUMN cc_mkt_desc cc_mkt_desc string COMMENT 'Market description';
ALTER TABLE call_center CHANGE COLUMN cc_market_manager cc_market_manager string COMMENT 'Market manager name';
ALTER TABLE call_center CHANGE COLUMN cc_division cc_division int COMMENT 'Division identifier code';
ALTER TABLE call_center CHANGE COLUMN cc_division_name cc_division_name string COMMENT 'Division name';
ALTER TABLE call_center CHANGE COLUMN cc_company cc_company int COMMENT 'Company identifier code';
ALTER TABLE call_center CHANGE COLUMN cc_company_name cc_company_name string COMMENT 'Company name';
ALTER TABLE call_center CHANGE COLUMN cc_street_number cc_street_number string COMMENT 'Street number of call center address';
ALTER TABLE call_center CHANGE COLUMN cc_street_name cc_street_name string COMMENT 'Street name of call center address';
ALTER TABLE call_center CHANGE COLUMN cc_street_type cc_street_type string COMMENT 'Street type (Ave, Blvd, St, etc.)';
ALTER TABLE call_center CHANGE COLUMN cc_suite_number cc_suite_number string COMMENT 'Suite number of call center address';
ALTER TABLE call_center CHANGE COLUMN cc_city cc_city string COMMENT 'City where call center is located';
ALTER TABLE call_center CHANGE COLUMN cc_county cc_county string COMMENT 'County where call center is located';
ALTER TABLE call_center CHANGE COLUMN cc_state cc_state string COMMENT 'State where call center is located (2-char abbreviation)';
ALTER TABLE call_center CHANGE COLUMN cc_zip cc_zip string COMMENT 'ZIP code of call center';
ALTER TABLE call_center CHANGE COLUMN cc_country cc_country string COMMENT 'Country where call center is located';
ALTER TABLE call_center CHANGE COLUMN cc_gmt_offset cc_gmt_offset decimal(5,2) COMMENT 'GMT timezone offset for call center location';
ALTER TABLE call_center CHANGE COLUMN cc_tax_percentage cc_tax_percentage decimal(5,2) COMMENT 'Tax percentage for call center location';

-- --------------------------------------------
-- WEB_SITE
-- --------------------------------------------
ALTER TABLE web_site SET TBLPROPERTIES ('comment' = 'Dimension table containing internet storefront configuration and management information');

ALTER TABLE web_site CHANGE COLUMN web_site_sk web_site_sk bigint COMMENT 'Surrogate key uniquely identifying each web site';
ALTER TABLE web_site CHANGE COLUMN web_site_id web_site_id string COMMENT 'Natural key - business identifier for the web site';
ALTER TABLE web_site CHANGE COLUMN web_rec_start_date web_rec_start_date string COMMENT 'SCD Type 2 - date this version became active';
ALTER TABLE web_site CHANGE COLUMN web_rec_end_date web_rec_end_date string COMMENT 'SCD Type 2 - date this version was superseded (null if current)';
ALTER TABLE web_site CHANGE COLUMN web_name web_name string COMMENT 'Web site name';
ALTER TABLE web_site CHANGE COLUMN web_open_date_sk web_open_date_sk bigint COMMENT 'FK to date_dim - date web site launched';
ALTER TABLE web_site CHANGE COLUMN web_close_date_sk web_close_date_sk bigint COMMENT 'FK to date_dim - date web site was closed (null if active)';
ALTER TABLE web_site CHANGE COLUMN web_class web_class string COMMENT 'Web site classification';
ALTER TABLE web_site CHANGE COLUMN web_manager web_manager string COMMENT 'Web site manager name';
ALTER TABLE web_site CHANGE COLUMN web_mkt_id web_mkt_id int COMMENT 'Market identifier code';
ALTER TABLE web_site CHANGE COLUMN web_mkt_class web_mkt_class string COMMENT 'Market classification';
ALTER TABLE web_site CHANGE COLUMN web_mkt_desc web_mkt_desc string COMMENT 'Market description';
ALTER TABLE web_site CHANGE COLUMN web_market_manager web_market_manager string COMMENT 'Market manager name';
ALTER TABLE web_site CHANGE COLUMN web_company_id web_company_id int COMMENT 'Company identifier code';
ALTER TABLE web_site CHANGE COLUMN web_company_name web_company_name string COMMENT 'Company name';
ALTER TABLE web_site CHANGE COLUMN web_street_number web_street_number string COMMENT 'Street number of web site headquarters';
ALTER TABLE web_site CHANGE COLUMN web_street_name web_street_name string COMMENT 'Street name of web site headquarters';
ALTER TABLE web_site CHANGE COLUMN web_street_type web_street_type string COMMENT 'Street type (Ave, Blvd, St, etc.)';
ALTER TABLE web_site CHANGE COLUMN web_suite_number web_suite_number string COMMENT 'Suite number of web site headquarters';
ALTER TABLE web_site CHANGE COLUMN web_city web_city string COMMENT 'City of web site headquarters';
ALTER TABLE web_site CHANGE COLUMN web_county web_county string COMMENT 'County of web site headquarters';
ALTER TABLE web_site CHANGE COLUMN web_state web_state string COMMENT 'State of web site headquarters (2-char abbreviation)';
ALTER TABLE web_site CHANGE COLUMN web_zip web_zip string COMMENT 'ZIP code of web site headquarters';
ALTER TABLE web_site CHANGE COLUMN web_country web_country string COMMENT 'Country of web site headquarters';
ALTER TABLE web_site CHANGE COLUMN web_gmt_offset web_gmt_offset decimal(5,2) COMMENT 'GMT timezone offset for web site headquarters';
ALTER TABLE web_site CHANGE COLUMN web_tax_percentage web_tax_percentage decimal(5,2) COMMENT 'Tax percentage for web site';

-- --------------------------------------------
-- WEB_PAGE
-- --------------------------------------------
ALTER TABLE web_page SET TBLPROPERTIES ('comment' = 'Dimension table containing individual web page references and attributes');

ALTER TABLE web_page CHANGE COLUMN wp_web_page_sk wp_web_page_sk bigint COMMENT 'Surrogate key uniquely identifying each web page';
ALTER TABLE web_page CHANGE COLUMN wp_web_page_id wp_web_page_id string COMMENT 'Natural key - business identifier for the web page';
ALTER TABLE web_page CHANGE COLUMN wp_rec_start_date wp_rec_start_date string COMMENT 'SCD Type 2 - date this version became active';
ALTER TABLE web_page CHANGE COLUMN wp_rec_end_date wp_rec_end_date string COMMENT 'SCD Type 2 - date this version was superseded (null if current)';
ALTER TABLE web_page CHANGE COLUMN wp_creation_date_sk wp_creation_date_sk bigint COMMENT 'FK to date_dim - date page was created';
ALTER TABLE web_page CHANGE COLUMN wp_access_date_sk wp_access_date_sk bigint COMMENT 'FK to date_dim - date page was last accessed';
ALTER TABLE web_page CHANGE COLUMN wp_autogen_flag wp_autogen_flag string COMMENT 'Y/N flag indicating if page was auto-generated';
ALTER TABLE web_page CHANGE COLUMN wp_customer_sk wp_customer_sk bigint COMMENT 'FK to customer - customer who owns this page (for personalized pages)';
ALTER TABLE web_page CHANGE COLUMN wp_url wp_url string COMMENT 'URL path of the web page';
ALTER TABLE web_page CHANGE COLUMN wp_type wp_type string COMMENT 'Page type (welcome, ad, product, order, etc.)';
ALTER TABLE web_page CHANGE COLUMN wp_char_count wp_char_count int COMMENT 'Character count on the page';
ALTER TABLE web_page CHANGE COLUMN wp_link_count wp_link_count int COMMENT 'Number of links on the page';
ALTER TABLE web_page CHANGE COLUMN wp_image_count wp_image_count int COMMENT 'Number of images on the page';
ALTER TABLE web_page CHANGE COLUMN wp_max_ad_count wp_max_ad_count int COMMENT 'Maximum number of ads allowed on the page';

-- --------------------------------------------
-- CATALOG_PAGE
-- --------------------------------------------
ALTER TABLE catalog_page SET TBLPROPERTIES ('comment' = 'Dimension table containing product catalog page information for catalog sales channel');

ALTER TABLE catalog_page CHANGE COLUMN cp_catalog_page_sk cp_catalog_page_sk bigint COMMENT 'Surrogate key uniquely identifying each catalog page';
ALTER TABLE catalog_page CHANGE COLUMN cp_catalog_page_id cp_catalog_page_id string COMMENT 'Natural key - business identifier for the catalog page';
ALTER TABLE catalog_page CHANGE COLUMN cp_start_date_sk cp_start_date_sk bigint COMMENT 'FK to date_dim - date catalog page became effective';
ALTER TABLE catalog_page CHANGE COLUMN cp_end_date_sk cp_end_date_sk bigint COMMENT 'FK to date_dim - date catalog page was discontinued';
ALTER TABLE catalog_page CHANGE COLUMN cp_department cp_department string COMMENT 'Department name for this catalog page';
ALTER TABLE catalog_page CHANGE COLUMN cp_catalog_number cp_catalog_number int COMMENT 'Catalog edition number';
ALTER TABLE catalog_page CHANGE COLUMN cp_catalog_page_number cp_catalog_page_number int COMMENT 'Page number within the catalog';
ALTER TABLE catalog_page CHANGE COLUMN cp_description cp_description string COMMENT 'Description of catalog page content';
ALTER TABLE catalog_page CHANGE COLUMN cp_type cp_type string COMMENT 'Catalog page type';

-- --------------------------------------------
-- PROMOTION
-- --------------------------------------------
ALTER TABLE promotion SET TBLPROPERTIES ('comment' = 'Dimension table containing marketing campaign and promotional offer definitions');

ALTER TABLE promotion CHANGE COLUMN p_promo_sk p_promo_sk bigint COMMENT 'Surrogate key uniquely identifying each promotion';
ALTER TABLE promotion CHANGE COLUMN p_promo_id p_promo_id string COMMENT 'Natural key - business identifier for the promotion';
ALTER TABLE promotion CHANGE COLUMN p_start_date_sk p_start_date_sk bigint COMMENT 'FK to date_dim - promotion start date';
ALTER TABLE promotion CHANGE COLUMN p_end_date_sk p_end_date_sk bigint COMMENT 'FK to date_dim - promotion end date';
ALTER TABLE promotion CHANGE COLUMN p_item_sk p_item_sk bigint COMMENT 'FK to item - specific item this promotion applies to (if any)';
ALTER TABLE promotion CHANGE COLUMN p_cost p_cost decimal(15,2) COMMENT 'Cost of running this promotion in dollars';
ALTER TABLE promotion CHANGE COLUMN p_response_target p_response_target int COMMENT 'Target number of responses for this promotion';
ALTER TABLE promotion CHANGE COLUMN p_promo_name p_promo_name string COMMENT 'Promotion name';
ALTER TABLE promotion CHANGE COLUMN p_channel_dmail p_channel_dmail string COMMENT 'Y/N flag indicating promotion via direct mail';
ALTER TABLE promotion CHANGE COLUMN p_channel_email p_channel_email string COMMENT 'Y/N flag indicating promotion via email';
ALTER TABLE promotion CHANGE COLUMN p_channel_catalog p_channel_catalog string COMMENT 'Y/N flag indicating promotion via catalog';
ALTER TABLE promotion CHANGE COLUMN p_channel_tv p_channel_tv string COMMENT 'Y/N flag indicating promotion via TV';
ALTER TABLE promotion CHANGE COLUMN p_channel_radio p_channel_radio string COMMENT 'Y/N flag indicating promotion via radio';
ALTER TABLE promotion CHANGE COLUMN p_channel_press p_channel_press string COMMENT 'Y/N flag indicating promotion via press';
ALTER TABLE promotion CHANGE COLUMN p_channel_event p_channel_event string COMMENT 'Y/N flag indicating promotion via event';
ALTER TABLE promotion CHANGE COLUMN p_channel_demo p_channel_demo string COMMENT 'Y/N flag indicating promotion via demonstration';
ALTER TABLE promotion CHANGE COLUMN p_channel_details p_channel_details string COMMENT 'Additional channel details';
ALTER TABLE promotion CHANGE COLUMN p_purpose p_purpose string COMMENT 'Purpose of the promotion';
ALTER TABLE promotion CHANGE COLUMN p_discount_active p_discount_active string COMMENT 'Y/N flag indicating if discount is currently active';

-- --------------------------------------------
-- SHIP_MODE
-- --------------------------------------------
ALTER TABLE ship_mode SET TBLPROPERTIES ('comment' = 'Dimension table containing delivery method specifications');

ALTER TABLE ship_mode CHANGE COLUMN sm_ship_mode_sk sm_ship_mode_sk bigint COMMENT 'Surrogate key uniquely identifying each ship mode';
ALTER TABLE ship_mode CHANGE COLUMN sm_ship_mode_id sm_ship_mode_id string COMMENT 'Natural key - business identifier for the ship mode';
ALTER TABLE ship_mode CHANGE COLUMN sm_type sm_type string COMMENT 'Shipping type (EXPRESS, LIBRARY, OVERNIGHT, REGULAR, TWO DAY)';
ALTER TABLE ship_mode CHANGE COLUMN sm_code sm_code string COMMENT 'Shipping code abbreviation';
ALTER TABLE ship_mode CHANGE COLUMN sm_carrier sm_carrier string COMMENT 'Carrier name (e.g., UPS, FedEx, USPS)';
ALTER TABLE ship_mode CHANGE COLUMN sm_contract sm_contract string COMMENT 'Contract identifier with carrier';

-- --------------------------------------------
-- REASON
-- --------------------------------------------
ALTER TABLE reason SET TBLPROPERTIES ('comment' = 'Dimension table containing return and transaction reason codes');

ALTER TABLE reason CHANGE COLUMN r_reason_sk r_reason_sk bigint COMMENT 'Surrogate key uniquely identifying each reason';
ALTER TABLE reason CHANGE COLUMN r_reason_id r_reason_id string COMMENT 'Natural key - business identifier for the reason';
ALTER TABLE reason CHANGE COLUMN r_reason_desc r_reason_desc string COMMENT 'Description of the reason (e.g., Did not like color, Found better price)';

-- --------------------------------------------
-- INCOME_BAND
-- --------------------------------------------
ALTER TABLE income_band SET TBLPROPERTIES ('comment' = 'Dimension table containing customer income classification ranges');

ALTER TABLE income_band CHANGE COLUMN ib_income_band_sk ib_income_band_sk bigint COMMENT 'Surrogate key uniquely identifying each income band';
ALTER TABLE income_band CHANGE COLUMN ib_lower_bound ib_lower_bound int COMMENT 'Lower bound of income range in dollars';
ALTER TABLE income_band CHANGE COLUMN ib_upper_bound ib_upper_bound int COMMENT 'Upper bound of income range in dollars';

-- --------------------------------------------
-- HOUSEHOLD_DEMOGRAPHICS
-- --------------------------------------------
ALTER TABLE household_demographics SET TBLPROPERTIES ('comment' = 'Dimension table containing household demographic profiles for segmentation');

ALTER TABLE household_demographics CHANGE COLUMN hd_demo_sk hd_demo_sk bigint COMMENT 'Surrogate key uniquely identifying each household demographic profile';
ALTER TABLE household_demographics CHANGE COLUMN hd_income_band_sk hd_income_band_sk bigint COMMENT 'FK to income_band - household income classification';
ALTER TABLE household_demographics CHANGE COLUMN hd_buy_potential hd_buy_potential string COMMENT 'Household buying potential category (Unknown, Low, Medium, High, 1001-5000, etc.)';
ALTER TABLE household_demographics CHANGE COLUMN hd_dep_count hd_dep_count int COMMENT 'Number of dependents in household';
ALTER TABLE household_demographics CHANGE COLUMN hd_vehicle_count hd_vehicle_count int COMMENT 'Number of vehicles owned by household';

-- ============================================
-- FACT TABLES
-- ============================================

-- --------------------------------------------
-- STORE_SALES
-- --------------------------------------------
ALTER TABLE store_sales SET TBLPROPERTIES ('comment' = 'Fact table recording individual line items from retail store transactions - largest fact table');

ALTER TABLE store_sales CHANGE COLUMN ss_sold_date_sk ss_sold_date_sk bigint COMMENT 'FK to date_dim - date when sale occurred';
ALTER TABLE store_sales CHANGE COLUMN ss_sold_time_sk ss_sold_time_sk bigint COMMENT 'FK to time_dim - time when sale occurred';
ALTER TABLE store_sales CHANGE COLUMN ss_item_sk ss_item_sk bigint COMMENT 'FK to item - product sold';
ALTER TABLE store_sales CHANGE COLUMN ss_customer_sk ss_customer_sk bigint COMMENT 'FK to customer - customer who made the purchase';
ALTER TABLE store_sales CHANGE COLUMN ss_cdemo_sk ss_cdemo_sk bigint COMMENT 'FK to customer_demographics - demographic profile at time of sale';
ALTER TABLE store_sales CHANGE COLUMN ss_hdemo_sk ss_hdemo_sk bigint COMMENT 'FK to household_demographics - household profile at time of sale';
ALTER TABLE store_sales CHANGE COLUMN ss_addr_sk ss_addr_sk bigint COMMENT 'FK to customer_address - customer address at time of sale';
ALTER TABLE store_sales CHANGE COLUMN ss_store_sk ss_store_sk bigint COMMENT 'FK to store - store where sale occurred';
ALTER TABLE store_sales CHANGE COLUMN ss_promo_sk ss_promo_sk bigint COMMENT 'FK to promotion - promotion applied to this sale (if any)';
ALTER TABLE store_sales CHANGE COLUMN ss_ticket_number ss_ticket_number bigint COMMENT 'Transaction ticket number grouping line items in same purchase';
ALTER TABLE store_sales CHANGE COLUMN ss_quantity ss_quantity int COMMENT 'Quantity of items purchased';
ALTER TABLE store_sales CHANGE COLUMN ss_wholesale_cost ss_wholesale_cost decimal(7,2) COMMENT 'Wholesale cost per unit in dollars';
ALTER TABLE store_sales CHANGE COLUMN ss_list_price ss_list_price decimal(7,2) COMMENT 'List price per unit in dollars';
ALTER TABLE store_sales CHANGE COLUMN ss_sales_price ss_sales_price decimal(7,2) COMMENT 'Actual sales price per unit in dollars';
ALTER TABLE store_sales CHANGE COLUMN ss_ext_discount_amt ss_ext_discount_amt decimal(7,2) COMMENT 'Extended discount amount: (list_price - sales_price) * quantity';
ALTER TABLE store_sales CHANGE COLUMN ss_ext_sales_price ss_ext_sales_price decimal(7,2) COMMENT 'Extended sales price: sales_price * quantity';
ALTER TABLE store_sales CHANGE COLUMN ss_ext_wholesale_cost ss_ext_wholesale_cost decimal(7,2) COMMENT 'Extended wholesale cost: wholesale_cost * quantity';
ALTER TABLE store_sales CHANGE COLUMN ss_ext_list_price ss_ext_list_price decimal(7,2) COMMENT 'Extended list price: list_price * quantity';
ALTER TABLE store_sales CHANGE COLUMN ss_ext_tax ss_ext_tax decimal(7,2) COMMENT 'Extended tax amount in dollars';
ALTER TABLE store_sales CHANGE COLUMN ss_coupon_amt ss_coupon_amt decimal(7,2) COMMENT 'Coupon discount amount in dollars';
ALTER TABLE store_sales CHANGE COLUMN ss_net_paid ss_net_paid decimal(7,2) COMMENT 'Net amount paid: ext_sales_price - coupon_amt';
ALTER TABLE store_sales CHANGE COLUMN ss_net_paid_inc_tax ss_net_paid_inc_tax decimal(7,2) COMMENT 'Net amount paid including tax: net_paid + ext_tax';
ALTER TABLE store_sales CHANGE COLUMN ss_net_profit ss_net_profit decimal(7,2) COMMENT 'Net profit: net_paid - ext_wholesale_cost';

-- --------------------------------------------
-- STORE_RETURNS
-- --------------------------------------------
ALTER TABLE store_returns SET TBLPROPERTIES ('comment' = 'Fact table tracking product returns from retail store sales channel');

ALTER TABLE store_returns CHANGE COLUMN sr_returned_date_sk sr_returned_date_sk bigint COMMENT 'FK to date_dim - date when return was processed';
ALTER TABLE store_returns CHANGE COLUMN sr_return_time_sk sr_return_time_sk bigint COMMENT 'FK to time_dim - time when return was processed';
ALTER TABLE store_returns CHANGE COLUMN sr_item_sk sr_item_sk bigint COMMENT 'FK to item - product returned';
ALTER TABLE store_returns CHANGE COLUMN sr_customer_sk sr_customer_sk bigint COMMENT 'FK to customer - customer who made the return';
ALTER TABLE store_returns CHANGE COLUMN sr_cdemo_sk sr_cdemo_sk bigint COMMENT 'FK to customer_demographics - demographic profile at time of return';
ALTER TABLE store_returns CHANGE COLUMN sr_hdemo_sk sr_hdemo_sk bigint COMMENT 'FK to household_demographics - household profile at time of return';
ALTER TABLE store_returns CHANGE COLUMN sr_addr_sk sr_addr_sk bigint COMMENT 'FK to customer_address - customer address at time of return';
ALTER TABLE store_returns CHANGE COLUMN sr_store_sk sr_store_sk bigint COMMENT 'FK to store - store where return was processed';
ALTER TABLE store_returns CHANGE COLUMN sr_reason_sk sr_reason_sk bigint COMMENT 'FK to reason - reason for the return';
ALTER TABLE store_returns CHANGE COLUMN sr_ticket_number sr_ticket_number bigint COMMENT 'Original transaction ticket number from store_sales';
ALTER TABLE store_returns CHANGE COLUMN sr_return_quantity sr_return_quantity int COMMENT 'Quantity of items returned';
ALTER TABLE store_returns CHANGE COLUMN sr_return_amt sr_return_amt decimal(7,2) COMMENT 'Return amount before tax in dollars';
ALTER TABLE store_returns CHANGE COLUMN sr_return_tax sr_return_tax decimal(7,2) COMMENT 'Tax amount on return in dollars';
ALTER TABLE store_returns CHANGE COLUMN sr_return_amt_inc_tax sr_return_amt_inc_tax decimal(7,2) COMMENT 'Return amount including tax: return_amt + return_tax';
ALTER TABLE store_returns CHANGE COLUMN sr_fee sr_fee decimal(7,2) COMMENT 'Restocking or processing fee in dollars';
ALTER TABLE store_returns CHANGE COLUMN sr_return_ship_cost sr_return_ship_cost decimal(7,2) COMMENT 'Shipping cost for return in dollars';
ALTER TABLE store_returns CHANGE COLUMN sr_refunded_cash sr_refunded_cash decimal(7,2) COMMENT 'Cash refunded to customer in dollars';
ALTER TABLE store_returns CHANGE COLUMN sr_reversed_charge sr_reversed_charge decimal(7,2) COMMENT 'Credit card charge reversed in dollars';
ALTER TABLE store_returns CHANGE COLUMN sr_store_credit sr_store_credit decimal(7,2) COMMENT 'Store credit issued in dollars';
ALTER TABLE store_returns CHANGE COLUMN sr_net_loss sr_net_loss decimal(7,2) COMMENT 'Net loss from return: return_amt + fee - refunded_cash - reversed_charge - store_credit';

-- --------------------------------------------
-- CATALOG_SALES
-- --------------------------------------------
ALTER TABLE catalog_sales SET TBLPROPERTIES ('comment' = 'Fact table recording individual line items from mail/catalog order transactions');

ALTER TABLE catalog_sales CHANGE COLUMN cs_sold_date_sk cs_sold_date_sk bigint COMMENT 'FK to date_dim - date when sale occurred';
ALTER TABLE catalog_sales CHANGE COLUMN cs_sold_time_sk cs_sold_time_sk bigint COMMENT 'FK to time_dim - time when sale occurred';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ship_date_sk cs_ship_date_sk bigint COMMENT 'FK to date_dim - date when order was shipped';
ALTER TABLE catalog_sales CHANGE COLUMN cs_bill_customer_sk cs_bill_customer_sk bigint COMMENT 'FK to customer - customer billed for the order';
ALTER TABLE catalog_sales CHANGE COLUMN cs_bill_cdemo_sk cs_bill_cdemo_sk bigint COMMENT 'FK to customer_demographics - billing customer demographic profile';
ALTER TABLE catalog_sales CHANGE COLUMN cs_bill_hdemo_sk cs_bill_hdemo_sk bigint COMMENT 'FK to household_demographics - billing customer household profile';
ALTER TABLE catalog_sales CHANGE COLUMN cs_bill_addr_sk cs_bill_addr_sk bigint COMMENT 'FK to customer_address - billing address';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ship_customer_sk cs_ship_customer_sk bigint COMMENT 'FK to customer - customer receiving the shipment';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ship_cdemo_sk cs_ship_cdemo_sk bigint COMMENT 'FK to customer_demographics - shipping customer demographic profile';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ship_hdemo_sk cs_ship_hdemo_sk bigint COMMENT 'FK to household_demographics - shipping customer household profile';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ship_addr_sk cs_ship_addr_sk bigint COMMENT 'FK to customer_address - shipping address';
ALTER TABLE catalog_sales CHANGE COLUMN cs_call_center_sk cs_call_center_sk bigint COMMENT 'FK to call_center - call center that processed the order';
ALTER TABLE catalog_sales CHANGE COLUMN cs_catalog_page_sk cs_catalog_page_sk bigint COMMENT 'FK to catalog_page - catalog page where item was featured';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ship_mode_sk cs_ship_mode_sk bigint COMMENT 'FK to ship_mode - shipping method used';
ALTER TABLE catalog_sales CHANGE COLUMN cs_warehouse_sk cs_warehouse_sk bigint COMMENT 'FK to warehouse - warehouse that fulfilled the order';
ALTER TABLE catalog_sales CHANGE COLUMN cs_item_sk cs_item_sk bigint COMMENT 'FK to item - product sold';
ALTER TABLE catalog_sales CHANGE COLUMN cs_promo_sk cs_promo_sk bigint COMMENT 'FK to promotion - promotion applied to this sale (if any)';
ALTER TABLE catalog_sales CHANGE COLUMN cs_order_number cs_order_number bigint COMMENT 'Order number grouping line items in same purchase';
ALTER TABLE catalog_sales CHANGE COLUMN cs_quantity cs_quantity int COMMENT 'Quantity of items purchased';
ALTER TABLE catalog_sales CHANGE COLUMN cs_wholesale_cost cs_wholesale_cost decimal(7,2) COMMENT 'Wholesale cost per unit in dollars';
ALTER TABLE catalog_sales CHANGE COLUMN cs_list_price cs_list_price decimal(7,2) COMMENT 'List price per unit in dollars';
ALTER TABLE catalog_sales CHANGE COLUMN cs_sales_price cs_sales_price decimal(7,2) COMMENT 'Actual sales price per unit in dollars';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ext_discount_amt cs_ext_discount_amt decimal(7,2) COMMENT 'Extended discount amount: (list_price - sales_price) * quantity';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ext_sales_price cs_ext_sales_price decimal(7,2) COMMENT 'Extended sales price: sales_price * quantity';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ext_wholesale_cost cs_ext_wholesale_cost decimal(7,2) COMMENT 'Extended wholesale cost: wholesale_cost * quantity';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ext_list_price cs_ext_list_price decimal(7,2) COMMENT 'Extended list price: list_price * quantity';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ext_tax cs_ext_tax decimal(7,2) COMMENT 'Extended tax amount in dollars';
ALTER TABLE catalog_sales CHANGE COLUMN cs_coupon_amt cs_coupon_amt decimal(7,2) COMMENT 'Coupon discount amount in dollars';
ALTER TABLE catalog_sales CHANGE COLUMN cs_ext_ship_cost cs_ext_ship_cost decimal(7,2) COMMENT 'Extended shipping cost in dollars';
ALTER TABLE catalog_sales CHANGE COLUMN cs_net_paid cs_net_paid decimal(7,2) COMMENT 'Net amount paid: ext_sales_price - coupon_amt';
ALTER TABLE catalog_sales CHANGE COLUMN cs_net_paid_inc_tax cs_net_paid_inc_tax decimal(7,2) COMMENT 'Net amount paid including tax: net_paid + ext_tax';
ALTER TABLE catalog_sales CHANGE COLUMN cs_net_paid_inc_ship cs_net_paid_inc_ship decimal(7,2) COMMENT 'Net amount paid including shipping: net_paid + ext_ship_cost';
ALTER TABLE catalog_sales CHANGE COLUMN cs_net_paid_inc_ship_tax cs_net_paid_inc_ship_tax decimal(7,2) COMMENT 'Net amount paid including shipping and tax: net_paid + ext_ship_cost + ext_tax';
ALTER TABLE catalog_sales CHANGE COLUMN cs_net_profit cs_net_profit decimal(7,2) COMMENT 'Net profit: net_paid - ext_wholesale_cost';

-- --------------------------------------------
-- CATALOG_RETURNS
-- --------------------------------------------
ALTER TABLE catalog_returns SET TBLPROPERTIES ('comment' = 'Fact table tracking product returns from catalog sales channel');

ALTER TABLE catalog_returns CHANGE COLUMN cr_returned_date_sk cr_returned_date_sk bigint COMMENT 'FK to date_dim - date when return was processed';
ALTER TABLE catalog_returns CHANGE COLUMN cr_returned_time_sk cr_returned_time_sk bigint COMMENT 'FK to time_dim - time when return was processed';
ALTER TABLE catalog_returns CHANGE COLUMN cr_item_sk cr_item_sk bigint COMMENT 'FK to item - product returned';
ALTER TABLE catalog_returns CHANGE COLUMN cr_refunded_customer_sk cr_refunded_customer_sk bigint COMMENT 'FK to customer - customer who received the refund';
ALTER TABLE catalog_returns CHANGE COLUMN cr_refunded_cdemo_sk cr_refunded_cdemo_sk bigint COMMENT 'FK to customer_demographics - refunded customer demographic profile';
ALTER TABLE catalog_returns CHANGE COLUMN cr_refunded_hdemo_sk cr_refunded_hdemo_sk bigint COMMENT 'FK to household_demographics - refunded customer household profile';
ALTER TABLE catalog_returns CHANGE COLUMN cr_refunded_addr_sk cr_refunded_addr_sk bigint COMMENT 'FK to customer_address - refund address';
ALTER TABLE catalog_returns CHANGE COLUMN cr_returning_customer_sk cr_returning_customer_sk bigint COMMENT 'FK to customer - customer who initiated the return';
ALTER TABLE catalog_returns CHANGE COLUMN cr_returning_cdemo_sk cr_returning_cdemo_sk bigint COMMENT 'FK to customer_demographics - returning customer demographic profile';
ALTER TABLE catalog_returns CHANGE COLUMN cr_returning_hdemo_sk cr_returning_hdemo_sk bigint COMMENT 'FK to household_demographics - returning customer household profile';
ALTER TABLE catalog_returns CHANGE COLUMN cr_returning_addr_sk cr_returning_addr_sk bigint COMMENT 'FK to customer_address - return shipping address';
ALTER TABLE catalog_returns CHANGE COLUMN cr_call_center_sk cr_call_center_sk bigint COMMENT 'FK to call_center - call center that processed the return';
ALTER TABLE catalog_returns CHANGE COLUMN cr_catalog_page_sk cr_catalog_page_sk bigint COMMENT 'FK to catalog_page - catalog page where item was originally featured';
ALTER TABLE catalog_returns CHANGE COLUMN cr_ship_mode_sk cr_ship_mode_sk bigint COMMENT 'FK to ship_mode - shipping method for return';
ALTER TABLE catalog_returns CHANGE COLUMN cr_warehouse_sk cr_warehouse_sk bigint COMMENT 'FK to warehouse - warehouse receiving the return';
ALTER TABLE catalog_returns CHANGE COLUMN cr_reason_sk cr_reason_sk bigint COMMENT 'FK to reason - reason for the return';
ALTER TABLE catalog_returns CHANGE COLUMN cr_order_number cr_order_number bigint COMMENT 'Original order number from catalog_sales';
ALTER TABLE catalog_returns CHANGE COLUMN cr_return_quantity cr_return_quantity int COMMENT 'Quantity of items returned';
ALTER TABLE catalog_returns CHANGE COLUMN cr_return_amount cr_return_amount decimal(7,2) COMMENT 'Return amount before tax in dollars';
ALTER TABLE catalog_returns CHANGE COLUMN cr_return_tax cr_return_tax decimal(7,2) COMMENT 'Tax amount on return in dollars';
ALTER TABLE catalog_returns CHANGE COLUMN cr_return_amt_inc_tax cr_return_amt_inc_tax decimal(7,2) COMMENT 'Return amount including tax: return_amount + return_tax';
ALTER TABLE catalog_returns CHANGE COLUMN cr_fee cr_fee decimal(7,2) COMMENT 'Restocking or processing fee in dollars';
ALTER TABLE catalog_returns CHANGE COLUMN cr_return_ship_cost cr_return_ship_cost decimal(7,2) COMMENT 'Shipping cost for return in dollars';
ALTER TABLE catalog_returns CHANGE COLUMN cr_refunded_cash cr_refunded_cash decimal(7,2) COMMENT 'Cash refunded to customer in dollars';
ALTER TABLE catalog_returns CHANGE COLUMN cr_reversed_charge cr_reversed_charge decimal(7,2) COMMENT 'Credit card charge reversed in dollars';
ALTER TABLE catalog_returns CHANGE COLUMN cr_store_credit cr_store_credit decimal(7,2) COMMENT 'Store credit issued in dollars';
ALTER TABLE catalog_returns CHANGE COLUMN cr_net_loss cr_net_loss decimal(7,2) COMMENT 'Net loss from return: return_amount + fee - refunded_cash - reversed_charge - store_credit';

-- --------------------------------------------
-- WEB_SALES
-- --------------------------------------------
ALTER TABLE web_sales SET TBLPROPERTIES ('comment' = 'Fact table recording individual line items from e-commerce web transactions');

ALTER TABLE web_sales CHANGE COLUMN ws_sold_date_sk ws_sold_date_sk bigint COMMENT 'FK to date_dim - date when sale occurred';
ALTER TABLE web_sales CHANGE COLUMN ws_sold_time_sk ws_sold_time_sk bigint COMMENT 'FK to time_dim - time when sale occurred';
ALTER TABLE web_sales CHANGE COLUMN ws_ship_date_sk ws_ship_date_sk bigint COMMENT 'FK to date_dim - date when order was shipped';
ALTER TABLE web_sales CHANGE COLUMN ws_item_sk ws_item_sk bigint COMMENT 'FK to item - product sold';
ALTER TABLE web_sales CHANGE COLUMN ws_bill_customer_sk ws_bill_customer_sk bigint COMMENT 'FK to customer - customer billed for the order';
ALTER TABLE web_sales CHANGE COLUMN ws_bill_cdemo_sk ws_bill_cdemo_sk bigint COMMENT 'FK to customer_demographics - billing customer demographic profile';
ALTER TABLE web_sales CHANGE COLUMN ws_bill_hdemo_sk ws_bill_hdemo_sk bigint COMMENT 'FK to household_demographics - billing customer household profile';
ALTER TABLE web_sales CHANGE COLUMN ws_bill_addr_sk ws_bill_addr_sk bigint COMMENT 'FK to customer_address - billing address';
ALTER TABLE web_sales CHANGE COLUMN ws_ship_customer_sk ws_ship_customer_sk bigint COMMENT 'FK to customer - customer receiving the shipment';
ALTER TABLE web_sales CHANGE COLUMN ws_ship_cdemo_sk ws_ship_cdemo_sk bigint COMMENT 'FK to customer_demographics - shipping customer demographic profile';
ALTER TABLE web_sales CHANGE COLUMN ws_ship_hdemo_sk ws_ship_hdemo_sk bigint COMMENT 'FK to household_demographics - shipping customer household profile';
ALTER TABLE web_sales CHANGE COLUMN ws_ship_addr_sk ws_ship_addr_sk bigint COMMENT 'FK to customer_address - shipping address';
ALTER TABLE web_sales CHANGE COLUMN ws_web_page_sk ws_web_page_sk bigint COMMENT 'FK to web_page - web page where purchase was made';
ALTER TABLE web_sales CHANGE COLUMN ws_web_site_sk ws_web_site_sk bigint COMMENT 'FK to web_site - web site where purchase was made';
ALTER TABLE web_sales CHANGE COLUMN ws_ship_mode_sk ws_ship_mode_sk bigint COMMENT 'FK to ship_mode - shipping method used';
ALTER TABLE web_sales CHANGE COLUMN ws_warehouse_sk ws_warehouse_sk bigint COMMENT 'FK to warehouse - warehouse that fulfilled the order';
ALTER TABLE web_sales CHANGE COLUMN ws_promo_sk ws_promo_sk bigint COMMENT 'FK to promotion - promotion applied to this sale (if any)';
ALTER TABLE web_sales CHANGE COLUMN ws_order_number ws_order_number bigint COMMENT 'Order number grouping line items in same purchase';
ALTER TABLE web_sales CHANGE COLUMN ws_quantity ws_quantity int COMMENT 'Quantity of items purchased';
ALTER TABLE web_sales CHANGE COLUMN ws_wholesale_cost ws_wholesale_cost decimal(7,2) COMMENT 'Wholesale cost per unit in dollars';
ALTER TABLE web_sales CHANGE COLUMN ws_list_price ws_list_price decimal(7,2) COMMENT 'List price per unit in dollars';
ALTER TABLE web_sales CHANGE COLUMN ws_sales_price ws_sales_price decimal(7,2) COMMENT 'Actual sales price per unit in dollars';
ALTER TABLE web_sales CHANGE COLUMN ws_ext_discount_amt ws_ext_discount_amt decimal(7,2) COMMENT 'Extended discount amount: (list_price - sales_price) * quantity';
ALTER TABLE web_sales CHANGE COLUMN ws_ext_sales_price ws_ext_sales_price decimal(7,2) COMMENT 'Extended sales price: sales_price * quantity';
ALTER TABLE web_sales CHANGE COLUMN ws_ext_wholesale_cost ws_ext_wholesale_cost decimal(7,2) COMMENT 'Extended wholesale cost: wholesale_cost * quantity';
ALTER TABLE web_sales CHANGE COLUMN ws_ext_list_price ws_ext_list_price decimal(7,2) COMMENT 'Extended list price: list_price * quantity';
ALTER TABLE web_sales CHANGE COLUMN ws_ext_tax ws_ext_tax decimal(7,2) COMMENT 'Extended tax amount in dollars';
ALTER TABLE web_sales CHANGE COLUMN ws_coupon_amt ws_coupon_amt decimal(7,2) COMMENT 'Coupon discount amount in dollars';
ALTER TABLE web_sales CHANGE COLUMN ws_ext_ship_cost ws_ext_ship_cost decimal(7,2) COMMENT 'Extended shipping cost in dollars';
ALTER TABLE web_sales CHANGE COLUMN ws_net_paid ws_net_paid decimal(7,2) COMMENT 'Net amount paid: ext_sales_price - coupon_amt';
ALTER TABLE web_sales CHANGE COLUMN ws_net_paid_inc_tax ws_net_paid_inc_tax decimal(7,2) COMMENT 'Net amount paid including tax: net_paid + ext_tax';
ALTER TABLE web_sales CHANGE COLUMN ws_net_paid_inc_ship ws_net_paid_inc_ship decimal(7,2) COMMENT 'Net amount paid including shipping: net_paid + ext_ship_cost';
ALTER TABLE web_sales CHANGE COLUMN ws_net_paid_inc_ship_tax ws_net_paid_inc_ship_tax decimal(7,2) COMMENT 'Net amount paid including shipping and tax: net_paid + ext_ship_cost + ext_tax';
ALTER TABLE web_sales CHANGE COLUMN ws_net_profit ws_net_profit decimal(7,2) COMMENT 'Net profit: net_paid - ext_wholesale_cost';

-- --------------------------------------------
-- WEB_RETURNS
-- --------------------------------------------
ALTER TABLE web_returns SET TBLPROPERTIES ('comment' = 'Fact table tracking product returns from web sales channel');

ALTER TABLE web_returns CHANGE COLUMN wr_returned_date_sk wr_returned_date_sk bigint COMMENT 'FK to date_dim - date when return was processed';
ALTER TABLE web_returns CHANGE COLUMN wr_returned_time_sk wr_returned_time_sk bigint COMMENT 'FK to time_dim - time when return was processed';
ALTER TABLE web_returns CHANGE COLUMN wr_item_sk wr_item_sk bigint COMMENT 'FK to item - product returned';
ALTER TABLE web_returns CHANGE COLUMN wr_refunded_customer_sk wr_refunded_customer_sk bigint COMMENT 'FK to customer - customer who received the refund';
ALTER TABLE web_returns CHANGE COLUMN wr_refunded_cdemo_sk wr_refunded_cdemo_sk bigint COMMENT 'FK to customer_demographics - refunded customer demographic profile';
ALTER TABLE web_returns CHANGE COLUMN wr_refunded_hdemo_sk wr_refunded_hdemo_sk bigint COMMENT 'FK to household_demographics - refunded customer household profile';
ALTER TABLE web_returns CHANGE COLUMN wr_refunded_addr_sk wr_refunded_addr_sk bigint COMMENT 'FK to customer_address - refund address';
ALTER TABLE web_returns CHANGE COLUMN wr_returning_customer_sk wr_returning_customer_sk bigint COMMENT 'FK to customer - customer who initiated the return';
ALTER TABLE web_returns CHANGE COLUMN wr_returning_cdemo_sk wr_returning_cdemo_sk bigint COMMENT 'FK to customer_demographics - returning customer demographic profile';
ALTER TABLE web_returns CHANGE COLUMN wr_returning_hdemo_sk wr_returning_hdemo_sk bigint COMMENT 'FK to household_demographics - returning customer household profile';
ALTER TABLE web_returns CHANGE COLUMN wr_returning_addr_sk wr_returning_addr_sk bigint COMMENT 'FK to customer_address - return shipping address';
ALTER TABLE web_returns CHANGE COLUMN wr_web_page_sk wr_web_page_sk bigint COMMENT 'FK to web_page - web page where original purchase was made';
ALTER TABLE web_returns CHANGE COLUMN wr_reason_sk wr_reason_sk bigint COMMENT 'FK to reason - reason for the return';
ALTER TABLE web_returns CHANGE COLUMN wr_order_number wr_order_number bigint COMMENT 'Original order number from web_sales';
ALTER TABLE web_returns CHANGE COLUMN wr_return_quantity wr_return_quantity int COMMENT 'Quantity of items returned';
ALTER TABLE web_returns CHANGE COLUMN wr_return_amt wr_return_amt decimal(7,2) COMMENT 'Return amount before tax in dollars';
ALTER TABLE web_returns CHANGE COLUMN wr_return_tax wr_return_tax decimal(7,2) COMMENT 'Tax amount on return in dollars';
ALTER TABLE web_returns CHANGE COLUMN wr_return_amt_inc_tax wr_return_amt_inc_tax decimal(7,2) COMMENT 'Return amount including tax: return_amt + return_tax';
ALTER TABLE web_returns CHANGE COLUMN wr_fee wr_fee decimal(7,2) COMMENT 'Restocking or processing fee in dollars';
ALTER TABLE web_returns CHANGE COLUMN wr_return_ship_cost wr_return_ship_cost decimal(7,2) COMMENT 'Shipping cost for return in dollars';
ALTER TABLE web_returns CHANGE COLUMN wr_refunded_cash wr_refunded_cash decimal(7,2) COMMENT 'Cash refunded to customer in dollars';
ALTER TABLE web_returns CHANGE COLUMN wr_reversed_charge wr_reversed_charge decimal(7,2) COMMENT 'Credit card charge reversed in dollars';
ALTER TABLE web_returns CHANGE COLUMN wr_account_credit wr_account_credit decimal(7,2) COMMENT 'Account credit issued in dollars';
ALTER TABLE web_returns CHANGE COLUMN wr_net_loss wr_net_loss decimal(7,2) COMMENT 'Net loss from return: return_amt + fee - refunded_cash - reversed_charge - account_credit';

-- --------------------------------------------
-- INVENTORY
-- --------------------------------------------
ALTER TABLE inventory SET TBLPROPERTIES ('comment' = 'Fact table modeling stock levels across warehouses for catalog and internet sales channels');

ALTER TABLE inventory CHANGE COLUMN inv_date_sk inv_date_sk bigint COMMENT 'FK to date_dim - date of inventory snapshot';
ALTER TABLE inventory CHANGE COLUMN inv_item_sk inv_item_sk bigint COMMENT 'FK to item - product in inventory';
ALTER TABLE inventory CHANGE COLUMN inv_warehouse_sk inv_warehouse_sk bigint COMMENT 'FK to warehouse - warehouse holding the inventory';
ALTER TABLE inventory CHANGE COLUMN inv_quantity_on_hand inv_quantity_on_hand int COMMENT 'Quantity of items on hand at this warehouse on this date';
