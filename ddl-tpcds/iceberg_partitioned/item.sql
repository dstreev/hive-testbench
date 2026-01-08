create database if not exists ${DB};
use ${DB};

drop table if exists item;

create table item
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.item;
