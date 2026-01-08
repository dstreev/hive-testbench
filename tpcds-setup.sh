#!/bin/bash

function usage {
	echo "Usage: tpcds-setup.sh --scale <scale_factor> [--dir <temp_directory>] [--no-part] [--external] [--format <serde_format>]"
	echo ""
	echo "Options:"
	echo "  --scale       Scale factor in GB (required)"
	echo "  --dir         HDFS directory containing generated data (default: /tmp/tpcds-generate)"
	echo "  --no-part     Create non-partitioned tables"
	echo "  --external    Create external tables instead of managed"
	echo "  --format      Table format: orc, parquet, rcfile (default: orc)"
	echo ""
	echo "Prerequisites:"
	echo "  1. Run tpcds-build.sh to build the data generator"
	echo "  2. Run tpcds-gen.sh --scale <scale> to generate the raw text data"
	echo ""
	exit 1
}

function runcommand {
	if [ "X$DEBUG_SCRIPT" != "X" ]; then
		eval "$1"
	else
		eval "$1" 2>/dev/null
	fi
	return $?
}

which hive > /dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Script must be run where Hive is installed"
	exit 1
fi

# Set up Hive command
HIVE="hive"

# Tables in the TPC-DS schema.
DIMS="date_dim time_dim item customer customer_demographics household_demographics customer_address store promotion warehouse ship_mode reason income_band call_center web_page catalog_page web_site"
FACTS="store_sales store_returns web_sales web_returns catalog_sales catalog_returns inventory"

# Defaults
STRATEGY="partitioned"
TYPE="managed"
FORMAT="orc"

while [[ $# -gt 0 ]]; do
  case "$1" in
    -D*)
      APP_JAVA_OPTS="${APP_JAVA_OPTS} ${1}"
      shift
      ;;
    --scale)
      shift
      SCALE=$1
      shift
      ;;
    --dir)
      shift
      DIR=$1
      shift
      ;;
    --no-part)
      shift
      STRATEGY="not_partitioned"
      ;;
    --external)
      shift
      TYPE="external"
      ;;
    --format)
      shift
      FORMAT=$1
      shift
      ;;
    *)
      PRG_ARGS="${PRG_ARGS} \"$1\""
      shift
  esac
done

# Get the parameters.
#SCALE=$1
#DIR=$2

if [ "X$BUCKET_DATA" != "X" ]; then
	BUCKETS=13
	RETURN_BUCKETS=13
else
	BUCKETS=1
	RETURN_BUCKETS=1
fi
if [ "X$DEBUG_SCRIPT" != "X" ]; then
	set -x
fi

# Sanity checking.
if [ "X$SCALE" = "X" ]; then
	usage
fi

if [ "X$DIR" = "X" ]; then
	DIR=/tmp/tpcds-generate
fi

if [ $SCALE -eq 1 ]; then
	echo "Scale factor must be greater than 1"
	exit 1
fi

if [ "$TYPE" = "external" ]; then
  LEGACY="true"
else
  LEGACY="false"
fi

# Verify HDFS connectivity first
echo "Checking HDFS connectivity..."
hdfs dfs -test -e / > /dev/null 2>&1
if [ $? -ne 0 ]; then
	echo ""
	echo "ERROR: Cannot connect to HDFS."
	echo ""
	echo "Possible causes:"
	echo "  - HDFS is not running or not accessible"
	echo "  - Hadoop configuration is missing or incorrect"
	echo "  - You don't have permission to access HDFS"
	echo ""
	echo "Please verify:"
	echo "  1. HDFS is running: 'hdfs dfsadmin -report'"
	echo "  2. Configuration is correct: check HADOOP_CONF_DIR or /etc/hadoop/conf"
	echo "  3. You can access HDFS: 'hdfs dfs -ls /'"
	echo ""
	exit 1
fi

# Check if the base directory exists and we have write permission
echo "Checking HDFS directory permissions..."
hdfs dfs -test -d ${DIR} > /dev/null 2>&1
if [ $? -ne 0 ]; then
	# Try to create the directory
	hdfs dfs -mkdir -p ${DIR} > /dev/null 2>&1
	if [ $? -ne 0 ]; then
		echo ""
		echo "ERROR: Cannot create HDFS directory: ${DIR}"
		echo ""
		echo "Possible causes:"
		echo "  - You don't have permission to create directories in the parent path"
		echo "  - The HDFS filesystem may be in safe mode"
		echo ""
		echo "Please verify:"
		echo "  1. Check your HDFS permissions: 'hdfs dfs -ls $(dirname ${DIR})'"
		echo "  2. Check if HDFS is in safe mode: 'hdfs dfsadmin -safemode get'"
		echo "  3. Contact your Hadoop administrator if needed"
		echo ""
		exit 1
	fi
fi

# Check if the generated data exists
echo "Checking for generated TPC-DS data at ${DIR}/${SCALE}..."
hdfs dfs -test -d ${DIR}/${SCALE} > /dev/null 2>&1
if [ $? -ne 0 ]; then
	echo ""
	echo "ERROR: Generated data not found at HDFS path: ${DIR}/${SCALE}"
	echo ""
	echo "The raw TPC-DS text data must be generated before running this setup script."
	echo ""
	echo "To generate the data, run:"
	echo "  ./tpcds-gen.sh --scale ${SCALE} --dir ${DIR}"
	echo ""
	echo "After generation completes, re-run this setup script:"
	echo "  ./tpcds-setup.sh --scale ${SCALE} --dir ${DIR}"
	echo ""
	exit 1
fi

# Verify at least one table directory exists in the generated data
TABLE_COUNT=$(hdfs dfs -ls ${DIR}/${SCALE} 2>/dev/null | grep -c "^d")
if [ "$TABLE_COUNT" -eq 0 ]; then
	echo ""
	echo "ERROR: No table directories found in ${DIR}/${SCALE}"
	echo ""
	echo "The data generation may have failed or is incomplete."
	echo "Please re-run the data generation:"
	echo "  ./tpcds-gen.sh --scale ${SCALE} --dir ${DIR}"
	echo ""
	exit 1
fi

echo "Found ${TABLE_COUNT} table directories in generated data."

# Check if the text database exists (created by tpcds-gen.sh)
echo "Checking for source text database: tpcds_text_${SCALE}..."
DB_EXISTS=$($HIVE -e "SHOW DATABASES LIKE 'tpcds_text_${SCALE}';" 2>/dev/null | grep -c "tpcds_text_${SCALE}")
if [ "$DB_EXISTS" -eq 0 ]; then
	echo ""
	echo "WARNING: Source database 'tpcds_text_${SCALE}' not found in Hive."
	echo ""
	echo "This database should have been created by tpcds-gen.sh."
	echo "The script will attempt to create the text tables first."
	echo ""
	echo "Running text table creation..."
	$HIVE -i settings/load-flat.sql -f ddl-tpcds/text/alltables.sql --hivevar DB=tpcds_text_${SCALE} --hivevar LOCATION=${DIR}/${SCALE}
	if [ $? -ne 0 ]; then
		echo ""
		echo "ERROR: Failed to create text tables."
		echo "Please check the Hive logs for more details."
		exit 1
	fi
	echo "Text tables created successfully."
fi

# Verify source tables actually exist by checking for date_dim (first table processed)
echo "Verifying source tables exist..."
TABLE_CHECK=$($HIVE -e "SHOW TABLES IN tpcds_text_${SCALE} LIKE 'date_dim';" 2>/dev/null | grep -c "date_dim")
if [ "$TABLE_CHECK" -eq 0 ]; then
	echo ""
	echo "ERROR: Source table 'tpcds_text_${SCALE}.date_dim' not found."
	echo ""
	echo "The text tables were not properly created. This can happen if:"
	echo "  1. tpcds-gen.sh failed during text table creation"
	echo "  2. The generated data at ${DIR}/${SCALE} is incomplete"
	echo ""
	echo "To fix this, try recreating the text tables manually:"
	echo "  hive -i settings/load-flat.sql -f ddl-tpcds/text/alltables.sql \\"
	echo "    --hivevar DB=tpcds_text_${SCALE} --hivevar LOCATION=${DIR}/${SCALE}"
	echo ""
	echo "Or regenerate the data:"
	echo "  ./tpcds-gen.sh --scale ${SCALE} --dir ${DIR}"
	echo ""
	exit 1
fi
echo "Source tables verified."

echo "Pre-flight checks passed. Starting table optimization..."
echo ""

LOAD_FILE="load_${STRATEGY}_${TYPE}_${FORMAT}_${SCALE}.mk"
SILENCE="2> /dev/null 1> /dev/null" 
if [ "X$DEBUG_SCRIPT" != "X" ]; then
	SILENCE=""
fi

echo -e "all: ${DIMS} ${FACTS}" > $LOAD_FILE

i=1
total=24

DATABASE=tpcds_bin_${STRATEGY}_${TYPE}_${FORMAT}_${SCALE}
DDL_DIR=bin_${STRATEGY}

echo -e "Running with... "
echo -e "      Database: ${DATABASE}"
echo -e "      Strategy: ${STRATEGY}"
echo -e "      Type:     ${TYPE}"
echo -e "      FORMAT:   ${FORMAT}"
echo -e "      SCALE:    ${SCALE}"

#DATABASE=tpcds_bin_partitioned_${FORMAT}_${SCALE}
MAX_REDUCERS=2500 # maximum number of useful reducers for any scale 
REDUCERS=$((test ${SCALE} -gt ${MAX_REDUCERS} && echo ${MAX_REDUCERS}) || echo ${SCALE})

# Populate the smaller tables.
for t in ${DIMS}
do
	COMMAND="$HIVE -i settings/load-partitioned.sql -f ddl-tpcds/bin_${STRATEGY}/${t}.sql \
	    --hivevar DB=${DATABASE} --hivevar SOURCE=tpcds_text_${SCALE} \
      --hivevar SCALE=${SCALE} --hivevar LEGACY=${LEGACY} \
	    --hivevar REDUCERS=${REDUCERS} \
	    --hivevar FILE=${FORMAT}"
	echo -e "${t}:\n\t@$COMMAND $SILENCE && echo 'Optimizing table $t ($i/$total).'" >> $LOAD_FILE
	i=`expr $i + 1`
done

for t in ${FACTS}
do
	COMMAND="$HIVE -i settings/load-partitioned.sql -f ddl-tpcds/bin_${STRATEGY}/${t}.sql \
	    --hivevar DB=${DATABASE} \
      --hivevar SCALE=${SCALE} --hivevar LEGACY=${LEGACY} \
	    --hivevar SOURCE=tpcds_text_${SCALE} --hivevar BUCKETS=${BUCKETS} \
	    --hivevar RETURN_BUCKETS=${RETURN_BUCKETS} --hivevar REDUCERS=${REDUCERS} --hivevar FILE=${FORMAT}"
	echo -e "${t}:\n\t@$COMMAND $SILENCE && echo 'Optimizing table $t ($i/$total).'" >> $LOAD_FILE
	i=`expr $i + 1`
done

make -j 1 -f $LOAD_FILE
MAKE_EXIT=$?

if [ $MAKE_EXIT -ne 0 ]; then
	echo ""
	echo "ERROR: Table optimization failed with exit code $MAKE_EXIT"
	echo ""
	echo "To see detailed errors, re-run with DEBUG_SCRIPT=1:"
	echo "  DEBUG_SCRIPT=1 ./tpcds-setup.sh --scale ${SCALE} --dir ${DIR}"
	echo ""
	echo "Common causes:"
	echo "  - Ranger/permission issues (see below)"
	echo "  - Source text tables missing or incomplete"
	echo "  - Hive metastore connectivity issues"
	echo ""
	echo "RANGER PERMISSIONS NOTE:"
	echo "  If you see 'Permission denied' or 'HiveAccessControlException' errors,"
	echo "  check Ranger policies for BOTH your user AND the 'hive' service user."
	echo "  The 'hive' user needs access to the HDFS location: ${DIR}/${SCALE}"
	echo "  The error message may show YOUR username, but the actual missing"
	echo "  permission could be for the 'hive' user accessing the source data."
	echo ""
	echo "  In Ranger, ensure these HDFS policies exist:"
	echo "    - Path: ${DIR} (recursive) - Users: $(whoami), hive - Permissions: rwx"
	echo ""
	echo "The generated makefile is: $LOAD_FILE"
	echo "You can inspect it and run individual table commands manually."
	echo ""
	exit 1
fi

echo "Loading constraints"
runcommand "$HIVE -f ddl-tpcds/bin_${STRATEGY}/add_constraints.sql --hivevar DB=${DATABASE}"

echo "Data loaded into database ${DATABASE}."
