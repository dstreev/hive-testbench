create database if not exists ${DB};
use ${DB};

drop table if exists promotion;

create table promotion
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.promotion;
