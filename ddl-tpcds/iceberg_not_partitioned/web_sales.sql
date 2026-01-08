create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists web_sales;

create table web_sales
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.web_sales;
