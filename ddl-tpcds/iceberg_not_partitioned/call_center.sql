create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists call_center;

create table call_center
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.call_center;
