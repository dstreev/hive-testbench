create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists store;

create table store
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.store;
