#!/bin/bash

# TPC-DS Data Generator Build Script
# This script builds the Java-based TPC-DS data generator

set -e

# Check for required programs
for f in javac; do
	which $f > /dev/null 2>&1
	if [ $? -ne 0 ]; then
		echo "Required program $f is missing. Please install or fix your path and try again."
		exit 1
	fi
done

# Check if Maven is installed
MVN=""
if which mvn > /dev/null 2>&1; then
	MVN="mvn"
elif [ -x "./apache-maven-3.9.6/bin/mvn" ]; then
	MVN="./apache-maven-3.9.6/bin/mvn"
else
	# Download and install Maven if not found
	echo "Maven not found, automatically installing it."
	MAVEN_VERSION="3.9.6"
	MAVEN_URL="https://downloads.apache.org/maven/maven-3/${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz"

	if [ ! -e "apache-maven-${MAVEN_VERSION}-bin.tar.gz" ]; then
		curl -O "$MAVEN_URL" 2> /dev/null
		if [ $? -ne 0 ]; then
			echo "Failed to download Maven, check Internet connectivity and try again."
			exit 1
		fi
	fi
	tar -zxf "apache-maven-${MAVEN_VERSION}-bin.tar.gz" > /dev/null
	MVN="./apache-maven-${MAVEN_VERSION}/bin/mvn"
fi

echo "Building TPC-DS Data Generator (Java)"

# Build the Java generator
cd tpcds-gen-java
$MVN clean package -DskipTests -q

if [ $? -ne 0 ]; then
	echo "Build failed!"
	exit 1
fi

cd ..

# Verify tpcds.idx distribution file exists
if [ ! -f "tpcds-gen-java/tpcds-tools/tpcds.idx" ]; then
	echo "Warning: tpcds.idx distribution file not found at tpcds-gen-java/tpcds-tools/tpcds.idx"
	exit 1
fi

echo ""
echo "TPC-DS Data Generator built successfully!"
echo ""
echo "JAR location: tpcds-gen-java/target/tpcds-gen-java-1.0-SNAPSHOT.jar"
echo "Distributions: tpcds-gen-java/tpcds-tools/tpcds.idx"
echo ""
echo "Usage examples:"
echo "  Local generation:"
echo "    java -jar tpcds-gen-java/target/tpcds-gen-java-1.0-SNAPSHOT.jar \\"
echo "      -s <scale> -d <output_dir> --distributions tpcds-gen-java/tpcds-tools/tpcds.idx"
echo ""
echo "  Hadoop distributed generation:"
echo "    hadoop jar tpcds-gen-java/target/tpcds-gen-java-1.0-SNAPSHOT.jar \\"
echo "      org.tpcds.hadoop.GenTableMR -s <scale> -d <hdfs_output_dir>"
echo ""
echo "You can now use tpcds-gen.sh to generate data or tpcds-setup.sh to set up Hive tables."
