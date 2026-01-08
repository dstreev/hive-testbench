create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists ship_mode;

create table ship_mode
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.ship_mode;
