create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists catalog_returns;

create table catalog_returns
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.catalog_returns;
