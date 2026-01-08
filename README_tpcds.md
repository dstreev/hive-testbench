hive-testbench (TPC-DS)
==============

A testbench for experimenting with Apache Hive at any data scale using the TPC-DS benchmark.

Overview
========

The hive-testbench is a data generator and set of queries that lets you experiment with Apache Hive at scale. The testbench allows you to experience base Hive performance on large datasets, and gives an easy way to see the impact of Hive tuning parameters and advanced settings.

This implementation uses a **pure Java TPC-DS data generator** that:
- Requires no native compilation (gcc not needed)
- Runs as a Hadoop MapReduce job for distributed data generation
- Produces TPC-DS compliant data at any scale factor

Prerequisites
=============

You will need:
* CDP 7.1.4+ or later cluster (7.1.4 required to support legacy CREATE for EXTERNAL tables)
* Apache Hive accessible via `hive` CLI
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

# 3. Create optimized tables from the generated data
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate

# 4. Run queries
cd sample-queries-tpcds
hive -i testbench.settings
hive> use tpcds_bin_partitioned_managed_orc_100;
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

**Options:**
| Option | Description | Default |
|--------|-------------|---------|
| `--scale` | Scale factor (must match generation) | - |
| `--dir` | HDFS directory with generated data | `/tmp/tpcds-generate` |
| `--no-part` | Create non-partitioned tables | (partitioned) |
| `--external` | Create external tables | (managed) |
| `--format` | Table format: orc, parquet, rcfile | `orc` |

**Examples:**
```bash
# Create managed, partitioned ORC tables (default)
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate

# Create external, partitioned ORC tables
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate --external

# Create non-partitioned Parquet tables
./tpcds-setup.sh --scale 100 --dir /tmp/tpcds-generate --no-part --format parquet
```

**What happens:**
1. Validates the source text tables exist
2. Creates optimized binary tables using CTAS (CREATE TABLE AS SELECT)
3. Applies partitioning for fact tables (unless `--no-part`)
4. Creates database with naming pattern: `tpcds_bin_<strategy>_<type>_<format>_<scale>`

**Database naming examples:**
```
tpcds_bin_partitioned_managed_orc_100
tpcds_bin_partitioned_external_orc_100
tpcds_bin_not_partitioned_managed_orc_100
tpcds_bin_not_partitioned_external_parquet_100
```

## Step 4: Create All Table Variants (Optional)

```bash
./tpcds-setup-all.sh --scale <scale_factor> --dir <hdfs_directory>
```

This creates all four database variants for performance comparison:
- `tpcds_bin_partitioned_managed_orc_<scale>`
- `tpcds_bin_partitioned_external_orc_<scale>`
- `tpcds_bin_not_partitioned_managed_orc_<scale>`
- `tpcds_bin_not_partitioned_external_orc_<scale>`

## Step 5: Run Queries

```bash
cd sample-queries-tpcds
hive -i testbench.settings
```

```sql
USE tpcds_bin_partitioned_managed_orc_100;
source query55.sql;
```

More than 99 TPC-DS queries are included in `sample-queries-tpcds/`.

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

Performance Testing
===================

After generating data, you can run performance comparisons across different table designs.

## Using time.sh

Run all TPC-DS queries against a specific database:
```bash
cd sample-queries-tpcds
./time.sh --db tpcds_bin_partitioned_managed_orc_100
```

## Using time_again.sh

Iterate over all 4 database variants multiple times:
```bash
./time_again.sh --scale 100 --iterations 3 --dir /local/results
```

This helps compare performance across:
- Managed vs External tables
- Partitioned vs Non-partitioned tables

## Analyzing Results

Output from `time_again.sh` can be loaded into HDFS and analyzed using queries in the `evaluate/` directory.

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

If you have improvements, pull requests are accepted.
