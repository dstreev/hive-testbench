/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.tables;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;

/**
 * Base class for all TPC-DS table generators.
 * Provides common functionality for row generation.
 *
 * @param <T> The row type for this table
 */
public abstract class BaseTable<T> implements Table<T> {

    protected final TableId tableId;
    protected final RandomNumberGenerator rng;
    protected final DistributionManager distMgr;
    protected final int scaleFactor;

    protected BaseTable(TableId tableId, RandomNumberGenerator rng,
                       DistributionManager distMgr, int scaleFactor) {
        this.tableId = tableId;
        this.rng = rng;
        this.distMgr = distMgr;
        this.scaleFactor = scaleFactor;
    }

    @Override
    public TableId getTableId() {
        return tableId;
    }

    @Override
    public String getName() {
        return tableId.getName();
    }

    @Override
    public String getAbbreviation() {
        return tableId.getAbbreviation();
    }

    /**
     * Helper to format nullable values.
     */
    protected String formatNullable(Object value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Helper to format nullable integers.
     */
    protected String formatNullableInt(Integer value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Helper to format nullable longs.
     */
    protected String formatNullableLong(Long value) {
        return value == null ? "" : value.toString();
    }

    /**
     * Helper to join values with a delimiter.
     */
    protected String join(String delimiter, Object... values) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.length; i++) {
            if (i > 0) {
                sb.append(delimiter);
            }
            sb.append(formatNullable(values[i]));
        }
        return sb.toString();
    }

    /**
     * Generate a surrogate key.
     */
    protected long generateSurrogateKey(long rowNumber) {
        return rowNumber;
    }

    /**
     * Generate a business key (ID string).
     */
    protected String generateBusinessKey(String prefix, long rowNumber, int width) {
        StringBuilder sb = new StringBuilder(prefix);
        String num = Long.toString(rowNumber);
        for (int i = num.length(); i < width; i++) {
            sb.append('0');
        }
        sb.append(num);
        return sb.toString();
    }
}
