create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists income_band;

create table income_band
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.income_band;
