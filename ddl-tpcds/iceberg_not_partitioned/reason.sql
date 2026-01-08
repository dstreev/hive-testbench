create database if not exists ${DB};
use ${DB};

drop table if exists reason;

create table reason
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.reason;
