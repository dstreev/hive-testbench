create database if not exists ${DB};
use ${DB};

set iceberg.mr.schema.auto.conversion=true;

drop table if exists catalog_page;

create table catalog_page
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.catalog_page;
