#!/bin/bash

# TPC-DS Data Generator Build Script
# This script builds the Java-based TPC-DS data generator

set -e

# Check that Java is available and at version 11+
if ! which java > /dev/null 2>&1; then
	echo "Java is not installed or not in PATH. Please install Java 11+ and try again."
	exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | head -n 1 | sed -E 's/.*"([0-9]+)(\.[0-9]+)*.*"/\1/')
if [ -z "$JAVA_VERSION" ]; then
	echo "Could not determine Java version. Please ensure Java 11+ is installed."
	exit 1
fi

if [ "$JAVA_VERSION" -lt 11 ] 2>/dev/null; then
	echo "Java version $JAVA_VERSION is not supported. Please install Java 11+ and try again."
	exit 1
fi

echo "Found Java version $JAVA_VERSION"

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

# Remove any old build artifacts to ensure a clean build
echo "Cleaning old build artifacts..."
rm -rf target/
rm -rf target-classes/

# Build with Maven clean to ensure fresh compilation
echo "Compiling..."
$MVN clean package -DskipTests -q

if [ $? -ne 0 ]; then
	echo "Build failed!"
	exit 1
fi

# Verify the JAR was created
if [ ! -f "target/tpcds-gen-java-1.0-SNAPSHOT.jar" ]; then
	echo "Build appeared to succeed but JAR file not found!"
	exit 1
fi

# Show JAR timestamp for verification
echo "JAR built: $(ls -la target/tpcds-gen-java-1.0-SNAPSHOT.jar | awk '{print $6, $7, $8}')"

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
