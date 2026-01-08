create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists store_sales;

create table store_sales
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.store_sales;
