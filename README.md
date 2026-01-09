Hive TestBench (TPC-DS) with Iceberg Support!!!
==============

A testbench for experimenting with Apache Hive at any data scale using the TPC-DS benchmark.

**Note:** This is a fork of the original [hive-testbench](https://github.com/hortonworks/hive-testbench) with TPC-DS support.

Overview
========

The hive-testbench is a data generator and set of queries that lets you experiment with Apache Hive at scale. The testbench allows you to experience base Hive performance on large datasets, and gives an easy way to see the impact of Hive tuning parameters and advanced settings.

TPC-DS
====================

The TPC-DS implementation features a **pure Java data generator** - a complete rewrite of the original C-based `dsdgen` tool. This Java implementation:

- **No native compilation required** - No gcc, no platform-specific builds
- **YARN/MapReduce integration** - Distributed data generation that scales with your cluster
- **Simplified deployment** - Single JAR file with all dependencies included
- **Enhanced parallelism** - Generate terabytes of data efficiently across cluster nodes
-
Table Creation Options
======================

This testbench supports multiple table formats and configurations, allowing you to benchmark and compare different storage strategies.

## Table Types

| Type | Option | Description |
|------|--------|-------------|
| **External** | `--external` (default) | Standard Hive external tables. Data persists independently of table metadata. Best for data sharing and flexibility. |
| **Iceberg** | `--iceberg` | Apache Iceberg tables with `STORED BY ICEBERG`. Provides ACID transactions, time travel, schema evolution, and partition evolution. |
| **ACID** | `--acid` | Managed ACID tables with full transactional support. Data is managed by Hive and deleted when table is dropped. |

## File Formats

| Format | Option | Description |
|--------|--------|-------------|
| **ORC** | `--format orc` (default) | Optimized Row Columnar format. Best compression and performance for Hive workloads. |
| **Parquet** | `--format parquet` | Apache Parquet format. Good interoperability with Spark, Impala, and other tools. |

## Partitioning Strategies

| Strategy | Option | Description |
|----------|--------|-------------|
| **Partitioned** | (default) | Fact tables partitioned by date columns for query optimization. |
| **Non-partitioned** | `--no-part` | All tables without partitioning. Simpler but may have slower query performance. |

## Comparison Matrix

| Configuration | Use Case | Pros | Cons |
|---------------|----------|------|------|
| External + ORC + Partitioned | General benchmarking | Fast queries, flexible data management | Manual partition management |
| External + Parquet + Partitioned | Multi-tool environments | Spark/Impala compatibility | Slightly larger files than ORC |
| Iceberg + ORC + Partitioned | Modern data lakehouse | ACID, time travel, schema evolution | Requires Iceberg-aware tools |
| Iceberg + ORC + Non-partitioned | Simple Iceberg testing | Easy setup, hidden partitioning available | May need partition evolution later |
| ACID + ORC + Partitioned | Transactional workloads | Full ACID, compaction | Higher overhead, Hive-only |

## Database Naming Convention

Databases are named following this pattern:
```
tpcds_<partitioning>_<type>_<format>_<scale>
```

Examples:
```
tpcds_partitioned_external_orc_100       # External ORC with partitioning (default)
tpcds_partitioned_iceberg_orc_100        # Iceberg ORC with partitioning
tpcds_not_partitioned_iceberg_orc_100    # Iceberg ORC without partitioning
tpcds_partitioned_acid_orc_100           # Managed ACID ORC with partitioning
tpcds_not_partitioned_external_parquet_100  # External Parquet without partitioning
```

Prerequisites
=============

It is recommended to use a cluster 'edge' node for this process to ensure all dependencies are available.  If the cluster is kerberized, ensure you have a valid Kerberos ticket before running the testbench.

There are various permission requirements for this testbench.  The default location for generated data is `/tmp/tpcds-generate`.  The user AND the 'hive' service user must have read/write/execute permissions for this directory.  If they don't, you will see errors like this when running the test bench hive sql files: [user] doesn't have execute permission on /tmp/tpcds-generate/date_dim.

You will need:
* CDP 7.1.4+ or later cluster (7.1.4 required to support legacy CREATE for EXTERNAL tables)
* Apache Hive accessible via `beeline|hive` CLI
* Hadoop/HDFS accessible via `hadoop` and `hdfs` CLI commands
* Java 11+ (for building and running the data generator)
* Maven (will be auto-downloaded if not present)
* MapReduce Framework jars deployed to HDFS (for distributed data generation)

**Important - MapReduce Framework Setup:**

The TPC-DS data generator uses MapReduce for distributed data generation. You must ensure the MapReduce framework jars are deployed to HDFS before running the generator:

In Cloudera Manager: **YARN > Actions > Install YARN MapReduce Framework Jars**

If this step is skipped, you will see an error like:
```
java.io.FileNotFoundException: File does not exist: hdfs://.../mr-framework.tar.gz
```

Quick Start
===========

```bash
# 1. Build the data generator
./tpcds-build.sh

# 2. Generate raw TPC-DS data (creates text tables)
./tpcds-gen.sh --scale 100 --dir /tmp/tpcds-generate

# 3. Create optimized tables from the generated data (default: external ORC tables)
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate

# 4. Run queries
cd sample-queries-tpcds
hive -i testbench.settings
hive> use tpcds_partitioned_external_orc_100;
hive> source query55.sql;
```

Detailed Installation Steps
===========================

## Step 1: Build the Data Generator

```bash
./tpcds-build.sh
```

This compiles and packages the pure Java TPC-DS data generator. The build:
- Cleans any previous build artifacts
- Compiles the Java source code
- Creates an uber JAR with all dependencies
- Verifies the JAR and distribution files are in place

Output: `tpcds-gen-java/target/tpcds-gen-java-1.0-SNAPSHOT.jar`

## Step 2: Generate Raw Data

```bash
./tpcds-gen.sh --scale <scale_factor> [options]
```

**Options:**
| Option | Description | Default |
|--------|-------------|---------|
| `--scale, -s` | Scale factor in GB (required) | - |
| `--dir, -d` | HDFS output directory | `/tmp/tpcds-generate` |
| `--parallel, -p` | Number of parallel mappers | Same as scale |
| `--distributions` | Path to tpcds.idx file | `tpcds-gen-java/tpcds-tools/tpcds.idx` |

**Examples:**
```bash
# Generate 100GB of data
./tpcds-gen.sh --scale 100

# Generate 1TB of data to a specific directory
./tpcds-gen.sh --scale 1000 --dir /data/tpcds

# Generate 10TB with custom parallelism
./tpcds-gen.sh --scale 10000 --parallel 500
```

**What happens:**
1. Validates HDFS connectivity and permissions
2. Launches a MapReduce job to generate TPC-DS data in parallel
3. Creates external Hive text tables pointing to the generated data
4. Creates database `tpcds_text_<scale>` with 24 tables

**Note:** Scale factor must be greater than 1 for distributed generation. For scale=1, use the JAR directly:
```bash
java -jar tpcds-gen-java/target/tpcds-gen-java-1.0-SNAPSHOT.jar \
  -s 1 -d /local/output --distributions tpcds-gen-java/tpcds-tools/tpcds.idx
```

## Step 3: Create Optimized Tables

```bash
./tpcds-setup.sh --scale <scale_factor> [options]
```

**General Options:**

| Option | Description | Default |
|--------|-------------|---------|
| `--scale` | Scale factor (must match generation) | - |
| `--dir` | HDFS directory with generated data | `/tmp/tpcds-generate` |

**Table Type (mutually exclusive):**

| Option | Description | Default |
|--------|-------------|---------|
| `--external` | Create external Hive tables | **Yes (default)** |
| `--iceberg` | Create Iceberg tables (STORED BY ICEBERG) | No |
| `--acid` | Create managed ACID tables | No |

**Format and Partitioning:**

| Option | Description | Default |
|--------|-------------|---------|
| `--format` | File format: `orc`, `parquet` | `orc` |
| `--no-part` | Create non-partitioned tables | (partitioned) |

**Examples:**
```bash
# Create external, partitioned ORC tables (default)
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate

# Create Iceberg tables with partitioning
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate --iceberg

# Create non-partitioned Iceberg tables
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate --iceberg --no-part

# Create Iceberg tables with Parquet format
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate --iceberg --format parquet

# Create managed ACID tables
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate --acid

# Create non-partitioned Parquet external tables
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate --no-part --format parquet
```

**What happens:**
1. Validates the source text tables exist
2. Creates optimized tables using CTAS (CREATE TABLE AS SELECT)
3. Applies partitioning for fact tables (unless `--no-part`)
4. For Iceberg tables, uses `STORED BY ICEBERG` and `PARTITIONED BY SPEC`
5. Creates database with naming pattern: `tpcds_<strategy>_<type>_<format>_<scale>`

## Step 4: Create All Table Variants (Optional)

```bash
./tpcds-setup-all.sh --scale <scale_factor> --dir <hdfs_directory>
```

This creates multiple database variants for performance comparison:
- `tpcds_partitioned_external_orc_<scale>` (External, partitioned)
- `tpcds_not_partitioned_external_orc_<scale>` (External, not partitioned)
- `tpcds_partitioned_iceberg_orc_<scale>` (Iceberg, partitioned)
- `tpcds_not_partitioned_iceberg_orc_<scale>` (Iceberg, not partitioned)

## Step 5: Run Queries

```bash
cd sample-queries-tpcds
hive -i testbench.settings
```

```sql
USE tpcds_partitioned_external_orc_100;
source query55.sql;
```

More than 99 TPC-DS queries are included in `sample-queries-tpcds/`.

Iceberg Tables
==============

When using `--iceberg`, tables are created with Apache Iceberg format which provides:

- **ACID Transactions**: Full transactional support with snapshot isolation
- **Time Travel**: Query historical versions of your data
- **Schema Evolution**: Add, drop, rename, or reorder columns without rewriting data
- **Partition Evolution**: Change partitioning strategy without data migration
- **Hidden Partitioning**: Partition by derived values (year, month, day) without partition columns in queries

**Partitioned Iceberg tables** use `PARTITIONED BY SPEC` for fact tables:
- `store_sales`, `store_returns` - partitioned by sold/returned date
- `catalog_sales`, `catalog_returns` - partitioned by sold/returned date
- `web_sales`, `web_returns` - partitioned by sold/returned date
- `inventory` - partitioned by date

**Non-partitioned Iceberg tables** use simple CTAS without partition specifications.

Both variants set `iceberg.mr.schema.auto.conversion=true` to handle schema conversion during table creation.

Troubleshooting
===============

## Ranger Permission Issues

If you see `HiveAccessControlException` or `Permission denied` errors:

**Check permissions for BOTH your user AND the 'hive' service user.**

The error may show your username, but the actual missing permission could be for the `hive` user accessing the source data location.

In Ranger, create an HDFS policy:
- **Path:** `/tmp/tpcds-generate` (recursive)
- **Users:** `<your_user>`, `hive`
- **Permissions:** Read, Write, Execute

## MapReduce Framework Issues

**Error:** `File does not exist: hdfs://.../mr-framework.tar.gz`

**Solution:** In Cloudera Manager: **YARN > Actions > Install YARN MapReduce Framework Jars**

**Error:** `Download and unpack failed` or `gzip: stdin: not in gzip format`

**Solution:** The framework archive is corrupted. Delete and reinstall:
```bash
hdfs dfs -rm -r /user/yarn/mapreduce/mr-framework/*.tar.gz
# Then reinstall via Cloudera Manager
```

## YARN User Directory Issues

**Error:** `Couldn't get userdir directory for <username>`

**Solutions:**
1. Ensure you have a valid home directory on all cluster nodes
2. Check `yarn.nodemanager.local-dirs` configuration in YARN
3. Verify NodeManager local directories have correct permissions (`yarn:hadoop`, mode `755`)

## Source Tables Not Found

**Error:** `Table not found 'date_dim'` during setup

**Solutions:**
1. Ensure `tpcds-gen.sh` completed successfully
2. Verify text database exists: `hive -e "SHOW DATABASES LIKE 'tpcds_text_*';"`
3. Recreate text tables manually:
   ```bash
   hive -i settings/load-flat.sql -f ddl-tpcds/text/alltables.sql \
     --hivevar DB=tpcds_text_<scale> --hivevar LOCATION=<hdfs_dir>/<scale>
   ```

## Debug Mode

For detailed error output, set `DEBUG_SCRIPT=1`:
```bash
DEBUG_SCRIPT=1 ./tpcds-gen.sh --scale 100
DEBUG_SCRIPT=1 ./tpcds-setup.sh --scale 100
```

Scale Factor Guidelines
=======================

| Scale Factor | Data Size | Recommended Cluster |
|--------------|-----------|---------------------|
| 10 | ~10 GB | Development/testing |
| 100 | ~100 GB | Small cluster (4-10 nodes) |
| 1000 | ~1 TB | Medium cluster |
| 10000 | ~10 TB | Large cluster |
| 100000 | ~100 TB | Very large cluster |

Generation time varies based on cluster size and available resources.

Feedback
========

If you have questions, comments or problems, [contact me](mailto:dstreever@cloudera.com).

