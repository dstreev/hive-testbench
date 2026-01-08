create database if not exists ${DB};
use ${DB};

drop table if exists inventory;

create table inventory
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.inventory;
