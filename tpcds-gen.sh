#!/bin/bash

# TPC-DS Data Generation Script
# Generates TPC-DS benchmark data and loads it into Hive text tables

function usage {
	echo "Usage: tpcds-gen.sh --scale <scale_factor> [--dir <temp_directory>] [--distributions <path>]"
	echo ""
	echo "Options:"
	echo "  --scale, -s       Scale factor in GB (required)"
	echo "  --dir, -d         HDFS output directory (default: /tmp/tpcds-generate)"
	echo "  --distributions   Path to tpcds.idx file (default: tpcds-gen-java/tpcds-tools/tpcds.idx)"
	echo "  --parallel, -p    Number of parallel mappers (default: scale factor)"
	echo ""
	exit 1
}

function runcommand {
	if [ "X$DEBUG_SCRIPT" != "X" ]; then
		$1
	else
		$1 2>/dev/null
	fi
}

# Check for the Java generator JAR
JAR_FILE="tpcds-gen-java/target/tpcds-gen-java-1.0-SNAPSHOT.jar"
if [ ! -f "$JAR_FILE" ]; then
	echo "Please build the data generator with ./tpcds-build.sh first"
	exit 1
fi

# Check for Hive and Hadoop
which hive > /dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Script must be run where Hive is installed"
	exit 1
fi

which hadoop > /dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Script must be run where Hadoop is installed"
	exit 1
fi

# Default distributions file
DISTRIBUTIONS="tpcds-gen-java/tpcds-tools/tpcds.idx"

# Parse arguments
while [[ $# -gt 0 ]]; do
  case "$1" in
    -D*)
      APP_JAVA_OPTS="${APP_JAVA_OPTS} ${1}"
      shift
      ;;
    --scale|-s)
      shift
      SCALE=$1
      shift
      ;;
    --dir|-d)
      shift
      DIR=$1
      shift
      ;;
    --distributions)
      shift
      DISTRIBUTIONS=$1
      shift
      ;;
    --parallel|-p)
      shift
      PARALLEL=$1
      shift
      ;;
    *)
      PRG_ARGS="${PRG_ARGS} \"$1\""
      shift
  esac
done

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

# Sanity checking
if [ "X$SCALE" = "X" ]; then
	usage
fi

if [ "X$DIR" = "X" ]; then
	DIR=/tmp/tpcds-generate
fi

if [ $SCALE -eq 1 ]; then
	echo "Scale factor must be greater than 1 for distributed generation."
	echo "For scale=1, use the standalone jar directly:"
	echo "  java -jar $JAR_FILE -s 1 -d <local_output_dir> --distributions $DISTRIBUTIONS"
	exit 1
fi

# Check for distributions file
if [ ! -f "$DISTRIBUTIONS" ]; then
	echo "Distributions file not found: $DISTRIBUTIONS"
	echo "Please specify --distributions <path/to/tpcds.idx>"
	exit 1
fi

# Set parallel to scale if not specified
if [ "X$PARALLEL" = "X" ]; then
	PARALLEL=$SCALE
fi

# Create output directory
hdfs dfs -mkdir -p ${DIR}

# Check if data already exists
hdfs dfs -ls ${DIR}/${SCALE} > /dev/null 2>&1
if [ $? -eq 0 ]; then
	echo "Data already exists at ${DIR}/${SCALE}"
	echo "Skipping generation. Delete the directory to regenerate."
else
	echo "Generating TPC-DS data at scale factor $SCALE"
	echo "Output directory: ${DIR}/${SCALE}"
	echo "Parallel streams: $PARALLEL"

	# Copy distributions file to HDFS for distributed access
	HDFS_DIST="/tmp/tpcds-distributions-$(date +%s).idx"
	hdfs dfs -put -f "$DISTRIBUTIONS" "$HDFS_DIST"

	# Run the Hadoop MapReduce job
	hadoop jar "$JAR_FILE" org.tpcds.hadoop.GenTableMR \
		-s $SCALE \
		-d ${DIR}/${SCALE} \
		-p $PARALLEL \
		-Dtpcds.distributions="$HDFS_DIST"

	# Cleanup temporary distributions file
	hdfs dfs -rm -f "$HDFS_DIST" 2>/dev/null

	if [ $? -ne 0 ]; then
		echo "Data generation failed!"
		exit 1
	fi
fi

# Verify data was generated
hdfs dfs -ls ${DIR}/${SCALE} > /dev/null 2>&1
if [ $? -ne 0 ]; then
	echo "Data generation failed, exiting."
	exit 1
fi

# Set permissions
hadoop fs -chmod -R 777 ${DIR}/${SCALE}

echo ""
echo "TPC-DS text data generation complete."
echo ""

# Create the text/flat tables as external tables
HIVE="hive"
echo "Loading text data into external Hive tables."
runcommand "$HIVE -i settings/load-flat.sql -f ddl-tpcds/text/alltables.sql --hivevar DB=tpcds_text_${SCALE} --hivevar LOCATION=${DIR}/${SCALE}"

echo ""
echo "Text data loaded into database tpcds_text_${SCALE}"
echo "Run tpcds-setup.sh to create optimized ORC tables."
