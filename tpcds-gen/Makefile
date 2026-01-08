MYOS ?= $(shell uname -s)

all: target/lib/dsdgen.jar target/tpcds-gen-1.0-SNAPSHOT.jar

target/tpcds-gen-1.0-SNAPSHOT.jar: $(shell find -name *.java) 
	mvn package

target/tpcds_kit.zip: tpcds_kit.zip
	mkdir -p target/
	cp tpcds_kit.zip target/tpcds_kit.zip

#tpcds_kit.zip:
#	curl http://dev.hortonworks.com.s3.amazonaws.com/hive-testbench/tpcds/README
#	curl --output tpcds_kit.zip http://dev.hortonworks.com.s3.amazonaws.com/hive-testbench/tpcds/TPCDS_Tools.zip

target/lib/dsdgen.jar: target/tools/dsdgen
	cd target/; mkdir -p lib/; ( jar cvf lib/dsdgen.jar tools/ || gjar cvf lib/dsdgen.jar tools/ )

target/tools/dsdgen: target/tpcds_kit.zip
	test -d target/tools/ || (cd target; unzip tpcds_kit.zip)
	test -d target/tools/ || (cd target; mv */tools tools)
	cd target/tools; cat ../../patches/all/*.patch | patch -p0
	cd target/tools; cat ../../patches/${MYOS}/*.patch | patch -p1 || true
	@# Create malloc.h shim for macOS (malloc is in stdlib.h)
	cd target/tools; mkdir -p include && echo '#include <stdlib.h>' > include/malloc.h
	@# Fix K&R function declarations for modern compilers (match definition lines only)
	cd target/tools; sed -i.bak 's/^getDateWeightFromJulian(jDay, nDistribution)$$/getDateWeightFromJulian(int jDay, int nDistribution)/' date.c
	cd target/tools; sed -i.bak 's/^getTdefFunctionsByNumber(nTable)$$/getTdefFunctionsByNumber(int nTable)/' tdef_functions.c
	cd target/tools; sed -i.bak 's/^getSimpleTdefsByNumber(nTable)$$/getSimpleTdefsByNumber(int nTable)/' tdefs.c
	cd target/tools; make clean; make dsdgen

clean:
	mvn clean
