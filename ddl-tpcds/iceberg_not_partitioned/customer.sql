create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists customer;

create table customer
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.customer;
