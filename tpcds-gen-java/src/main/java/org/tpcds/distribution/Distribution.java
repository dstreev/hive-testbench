/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds.distribution;

/**
 * Represents a distribution of weighted values.
 * Used for generating random values according to predefined distributions.
 */
public class Distribution {

    public static final int TKN_VARCHAR = 5;
    public static final int TKN_INT = 7;
    public static final int TKN_DATE = 9;
    public static final int TKN_DECIMAL = 10;

    private int[] typeVector;       // Type of each value set
    private int[][] weightSets;     // Cumulative weights for each weight set
    private int[] maximums;         // Maximum cumulative weight for each weight set
    private int[][] valueSets;      // Offsets into strings for each value
    private String strings;         // All string values concatenated
    private String names;           // Column aliases
    private int size;               // Number of entries

    public Distribution() {
    }

    // Getters and setters
    public int[] getTypeVector() {
        return typeVector;
    }

    public void setTypeVector(int[] typeVector) {
        this.typeVector = typeVector;
    }

    public int[][] getWeightSets() {
        return weightSets;
    }

    public void setWeightSets(int[][] weightSets) {
        this.weightSets = weightSets;
    }

    public int[] getMaximums() {
        return maximums;
    }

    public void setMaximums(int[] maximums) {
        this.maximums = maximums;
    }

    public int[][] getValueSets() {
        return valueSets;
    }

    public void setValueSets(int[][] valueSets) {
        this.valueSets = valueSets;
    }

    public String getStrings() {
        return strings;
    }

    public void setStrings(String strings) {
        this.strings = strings;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    /**
     * Get the maximum weight for a weight set (1-based index).
     */
    public int getMaxWeight(int wset) {
        return maximums[wset - 1];
    }

    /**
     * Get a string value at the specified offset.
     */
    public String getStringValue(int offset) {
        if (strings == null || offset < 0 || offset >= strings.length()) {
            return "";
        }
        // Find the end of the null-terminated string
        int end = strings.indexOf('\0', offset);
        if (end < 0) {
            end = strings.length();
        }
        return strings.substring(offset, end);
    }

    /**
     * Get the type of a value set (1-based index).
     */
    public int getType(int vset) {
        return typeVector[vset - 1];
    }

    /**
     * Get the value offset for a specific row and value set.
     */
    public int getValueOffset(int row, int vset) {
        return valueSets[vset - 1][row];
    }

    /**
     * Get the cumulative weight for a specific row and weight set.
     */
    public int getCumulativeWeight(int row, int wset) {
        return weightSets[wset - 1][row];
    }
}
