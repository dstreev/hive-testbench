create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists date_dim;

create table date_dim
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.date_dim;
