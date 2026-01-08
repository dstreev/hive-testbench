#!/bin/bash

# Build dataset and supply tables first.
. ./tpcds-gen.sh $@

echo -e "Running setup for: External Partitioned, ORC"
echo "Output going to: /tmp/ext_part_orc.log"
nohup ./tpcds-setup.sh $@ > /tmp/ext_part_orc.log 2>&1 &

echo -e "Running setup for: External Non-Partitioned, ORC"
echo "Output going to: /tmp/ext_no-part_orc.log"
nohup ./tpcds-setup.sh $@ --no-part > /tmp/ext_no-part_orc.log 2>&1 &

echo -e "Running setup for: Iceberg Partitioned, ORC"
echo "Output going to: /tmp/iceberg_part_orc.log"
nohup ./tpcds-setup.sh $@ --iceberg > /tmp/iceberg_part_orc.log 2>&1 &

echo -e "Running setup for: Iceberg Non-Partitioned, ORC"
echo "Output going to: /tmp/iceberg_no-part_orc.log"
nohup ./tpcds-setup.sh $@ --no-part --iceberg > /tmp/iceberg_no-part_orc.log 2>&1 &
