create database if not exists ${DB};
use ${DB};

drop table if exists web_site;

create table web_site
stored by iceberg
stored as ${FILE}
as select * from ${SOURCE}.web_site;
