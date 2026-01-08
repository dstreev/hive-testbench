/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds.output;

import org.tpcds.tables.Table;

import java.io.*;

/**
 * Writes table data to output files.
 */
public class TableWriter {

    private final String outputDir;
    private final String delimiter;
    private final String suffix;

    public TableWriter(String outputDir, String delimiter, String suffix) {
        this.outputDir = outputDir;
        this.delimiter = delimiter;
        this.suffix = suffix;
    }

    public TableWriter(String outputDir) {
        this(outputDir, "|", ".dat");
    }

    /**
     * Generate all rows for a table and write to file.
     */
    public <T> void writeTable(Table<T> table, int scaleFactor) throws IOException {
        String filename = outputDir + File.separator + table.getName() + suffix;
        long rowCount = table.getRowCount(scaleFactor);

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(filename), 64 * 1024))) {

            for (long i = 1; i <= rowCount; i++) {
                T row = table.buildRow(i);
                String line = table.formatRow(row, delimiter);
                writer.println(line + delimiter);

                // Progress indicator every million rows
                if (i % 1000000 == 0) {
                    System.out.printf("%s: %,d / %,d rows%n", table.getName(), i, rowCount);
                }
            }
        }

        System.out.printf("Generated %s: %,d rows%n", filename, rowCount);
    }

    /**
     * Generate rows for a range (for parallel generation).
     */
    public <T> void writeTableRange(Table<T> table, int scaleFactor, long startRow, long endRow,
                                    int childNum) throws IOException {
        String filename = outputDir + File.separator + table.getName() + "_" + childNum + suffix;

        try (PrintWriter writer = new PrintWriter(new BufferedWriter(
                new FileWriter(filename), 64 * 1024))) {

            for (long i = startRow; i <= endRow; i++) {
                T row = table.buildRow(i);
                String line = table.formatRow(row, delimiter);
                writer.println(line + delimiter);
            }
        }

        System.out.printf("Generated %s: rows %,d-%,d%n", filename, startRow, endRow);
    }
}
