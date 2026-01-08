create database if not exists ${DB};
use ${DB};

drop table if exists catalog_page;

create table catalog_page
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.catalog_page;
