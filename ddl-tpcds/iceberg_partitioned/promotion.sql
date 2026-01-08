create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists promotion;

create table promotion
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.promotion;
