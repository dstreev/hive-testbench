create database if not exists ${DB};
use ${DB};

drop table if exists date_dim;

create table date_dim
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.date_dim;
