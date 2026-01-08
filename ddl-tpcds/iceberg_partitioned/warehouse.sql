create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists warehouse;

create table warehouse
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.warehouse;
