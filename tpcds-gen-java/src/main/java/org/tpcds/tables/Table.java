/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds.tables;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.distribution.DistributionManager;

/**
 * Interface for all TPC-DS table generators.
 * Each table implements this interface to generate rows.
 *
 * @param <T> The row type for this table
 */
public interface Table<T> {

    /**
     * Get the table identifier.
     */
    TableId getTableId();

    /**
     * Get the table name.
     */
    String getName();

    /**
     * Get the table abbreviation.
     */
    String getAbbreviation();

    /**
     * Get the number of rows to generate at a given scale factor.
     */
    long getRowCount(int scaleFactor);

    /**
     * Build a row for the given row number.
     *
     * @param rowNumber The row number (1-based)
     * @return The generated row
     */
    T buildRow(long rowNumber);

    /**
     * Get the column names for this table.
     */
    String[] getColumnNames();

    /**
     * Format a row as a delimited string.
     *
     * @param row The row to format
     * @param delimiter The field delimiter
     * @return The formatted row string
     */
    String formatRow(T row, String delimiter);

    /**
     * Format a row as a delimited string (type-erased version for generic use).
     *
     * @param row The row to format (must be of type T)
     * @param delimiter The field delimiter
     * @return The formatted row string
     */
    @SuppressWarnings("unchecked")
    default String formatRowObject(Object row, String delimiter) {
        return formatRow((T) row, delimiter);
    }

    /**
     * Check if this table is date-based (row count varies by date range).
     */
    default boolean isDateBased() {
        return false;
    }

    /**
     * Check if this table has a parent table (for parent/child relationships).
     */
    default Table<?> getParentTable() {
        return null;
    }

    /**
     * Check if this table has a child table.
     */
    default Table<?> getChildTable() {
        return null;
    }
}
