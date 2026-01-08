/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds.core;

/**
 * Represents a single random number stream used for generating deterministic random values.
 * Each column in the TPC-DS schema uses its own stream to ensure reproducibility.
 */
public class RandomNumberStream {
    private int used;
    private int usedPerRow;
    private long seed;
    private final long initialSeed;
    private final int column;
    private final int table;
    private final int duplicateOf;
    private long total;

    public RandomNumberStream(int column, int table, int duplicateOf, long initialSeed) {
        this.column = column;
        this.table = table;
        this.duplicateOf = duplicateOf;
        this.initialSeed = initialSeed;
        this.seed = initialSeed;
        this.used = 0;
        this.usedPerRow = 0;
        this.total = 0;
    }

    public int getUsed() {
        return used;
    }

    public void incrementUsed() {
        this.used++;
        this.total++;
    }

    public int getUsedPerRow() {
        return usedPerRow;
    }

    public void setUsedPerRow(int usedPerRow) {
        this.usedPerRow = usedPerRow;
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public long getInitialSeed() {
        return initialSeed;
    }

    public int getColumn() {
        return column;
    }

    public int getTable() {
        return table;
    }

    public int getDuplicateOf() {
        return duplicateOf;
    }

    public long getTotal() {
        return total;
    }

    public void reset() {
        this.seed = this.initialSeed;
        this.used = 0;
    }
}
