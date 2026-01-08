create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists catalog_sales;

create table catalog_sales
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.catalog_sales;
