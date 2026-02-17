# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Hive TestBench — a TPC-DS benchmarking suite for Apache Hive with Iceberg support. Features a pure Java port of the TPC-DS `dsdgen` data generator (no C compilation needed), YARN/MapReduce distributed data generation, and support for multiple table formats (External, Iceberg, ACID) with ORC or Parquet storage.

## Build & Run Commands

**Build the Java data generator:**
```bash
./tpcds-build.sh
```
This runs `mvn clean package -DskipTests` on the `tpcds-gen-java` module, producing an uber JAR at `tpcds-gen-java/target/tpcds-gen-java-1.0-SNAPSHOT.jar`.

**Run tests:**
```bash
cd tpcds-gen-java && mvn test
```

**Maven build directly:**
```bash
mvn clean package -DskipTests    # from project root
mvn clean package -DskipTests -pl tpcds-gen-java   # specific module
```

**Data generation and table setup (requires Hadoop cluster):**
```bash
./tpcds-gen.sh --scale 100 --dir /tmp/tpcds-generate
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate [--iceberg|--external|--acid] [--format orc|parquet] [--no-part]
./tpcds-setup-all.sh --scale 100 --dir /tmp/tpcds-generate   # creates all 4 variants
```

**Debug mode for shell scripts:** prefix with `DEBUG_SCRIPT=1`.

## Architecture

### Java Data Generator (`tpcds-gen-java/`)

Single Maven module producing an uber JAR. Package: `com.streever.utils.hive.testbench.tpcds`.

- **`DsGen.java`** — CLI entry point using picocli. Supports local generation (`java -jar`) and Hadoop delegation (`hadoop jar ... org.tpcds.hadoop.GenTableMR`).
- **`tables/`** — 24 TPC-DS table implementations. Each table class (e.g., `StoreSalesTable`, `DateDimTable`) extends `BaseTable` implementing `Table<?>`. `TableId` enum lists all tables; `TableFactory` creates them.
- **`tables/rows/`** — Row data objects for each table, one per table class.
- **`core/`** — `RandomNumberGenerator` and `RandomNumberStream` for deterministic TPC-DS data generation.
- **`distribution/`** — `DistributionManager` loads `tpcds.idx` statistical distribution file; `Distribution` and `DistributionIndex` model the distributions.
- **`types/`** — `DsDate` and `DsDecimal` custom types matching TPC-DS spec.
- **`output/TableWriter.java`** — Writes generated rows to delimited files.
- **`hadoop/GenTableMR.java`** — MapReduce job for distributed data generation across cluster nodes.

### DDL and SQL (`ddl-tpcds/`)

Four DDL variants for creating optimized Hive tables from generated text data:
- `bin_partitioned/` — External ORC with INSERT OVERWRITE partitions
- `bin_not_partitioned/` — External ORC with simple CTAS
- `iceberg_partitioned/` — Iceberg with `PARTITIONED BY SPEC`
- `iceberg_not_partitioned/` — Iceberg CTAS without partition spec

Each directory has one `.sql` file per table. DDL files use Hive `--hivevar` substitution for `DB`, `SOURCE`, `SCALE`, `LEGACY`, `FILE`, `REDUCERS`.

### Shell Script Orchestration

`tpcds-setup.sh` generates a Makefile (`load_*.mk`) with one target per table, then runs `make -j 1` to execute all Hive DDL commands sequentially. Database naming convention: `tpcds_<strategy>_<type>_<format>_<scale>` (e.g., `tpcds_partitioned_iceberg_orc_100`).

### Query Sets

- `sample-queries-tpcds/` — 99 Hive-compatible TPC-DS queries (use with `hive -i testbench.settings`)
- `spark-queries-tpcds/` — 99 Spark-compatible TPC-DS queries (sourced from Apache Spark)

## Key Technical Details

- Java 11+ required; Hadoop dependencies are `provided` scope (supplied by cluster at runtime)
- The `tpcds.idx` distribution file at `tpcds-gen-java/tpcds-tools/tpcds.idx` is required for data generation
- Scale factor must be >1 for distributed MapReduce generation; scale=1 uses local JAR execution
- External tables require `LEGACY=true` for CTAS; Iceberg and ACID use `LEGACY=false`
- Iceberg tables set `iceberg.mr.schema.auto.conversion=true` during creation