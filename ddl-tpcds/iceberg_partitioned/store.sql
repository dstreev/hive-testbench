create database if not exists ${DB};
use ${DB};

drop table if exists store;

create table store
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.store;
