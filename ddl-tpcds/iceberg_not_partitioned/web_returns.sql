create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists web_returns;

create table web_returns
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.web_returns;
