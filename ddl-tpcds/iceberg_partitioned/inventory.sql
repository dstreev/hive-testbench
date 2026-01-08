create database if not exists ${DB};
use ${DB};

drop table if exists inventory;

create table inventory
(
      inv_date_sk bigint
,     inv_item_sk bigint
,     inv_warehouse_sk bigint
,     inv_quantity_on_hand int
)
partitioned by spec (inv_date_sk)
stored by iceberg
stored as ${FILE};

insert into inventory
select
        inv.inv_date_sk,
        inv.inv_item_sk,
        inv.inv_warehouse_sk,
        inv.inv_quantity_on_hand
from ${SOURCE}.inventory inv;
