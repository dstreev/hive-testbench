create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists time_dim;

create table time_dim
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.time_dim;
