create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists item;

create table item
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.item;
