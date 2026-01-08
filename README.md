hive-testbench
==============

A testbench for experimenting with Apache Hive at any data scale.

Overview
========

The hive-testbench is a data generator and set of queries that lets you experiment with Apache Hive at scale. It includes implementations for both TPC-DS and TPC-H benchmarks.

TPC-DS (Recommended)
====================

The TPC-DS implementation features a **pure Java data generator** - a complete rewrite of the original C-based `dsdgen` tool. This Java implementation:

- **No native compilation required** - No gcc, no platform-specific builds
- **YARN/MapReduce integration** - Distributed data generation that scales with your cluster
- **Simplified deployment** - Single JAR file with all dependencies included
- **Enhanced parallelism** - Generate terabytes of data efficiently across cluster nodes

See **[README_tpcds.md](./README_tpcds.md)** for complete documentation including:
- Quick start guide
- Detailed installation steps
- All command-line options
- Troubleshooting guide
- Performance testing instructions

**Quick Start:**
```bash
./tpcds-build.sh                                    # Build the generator
./tpcds-gen.sh --scale 100                          # Generate 100GB of data
./tpcds-setup.sh --scale 100                        # Create optimized Hive tables
```

TPC-H
=====

The TPC-H implementation uses the standard C-based data generator.

```bash
./tpch-build.sh                                     # Build (requires gcc)
./tpch-setup.sh 1000                                # Generate 1TB and create tables
```

See `sample-queries-tpch/` for TPC-H benchmark queries.

Feedback
========

If you have questions, comments or problems, [contact me](mailto:dstreever@cloudera.com).

If you have improvements, pull requests are accepted.
