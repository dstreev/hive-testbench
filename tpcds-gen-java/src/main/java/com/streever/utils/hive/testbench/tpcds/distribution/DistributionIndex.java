/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.distribution;

/**
 * Index entry for a distribution in the .idx file.
 */
public class DistributionIndex {

    public static final int D_NAME_LEN = 20;
    public static final int FL_LOADED = 0x01;

    private String name;
    private int index;
    private int offset;
    private int strSpace;
    private int nameSpace;
    private int length;
    private int wWidth;     // Number of weight sets
    private int vWidth;     // Number of value sets
    private int flags;
    private Distribution dist;

    public DistributionIndex() {
    }

    // Getters and setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public int getStrSpace() {
        return strSpace;
    }

    public void setStrSpace(int strSpace) {
        this.strSpace = strSpace;
    }

    public int getNameSpace() {
        return nameSpace;
    }

    public void setNameSpace(int nameSpace) {
        this.nameSpace = nameSpace;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWWidth() {
        return wWidth;
    }

    public void setWWidth(int wWidth) {
        this.wWidth = wWidth;
    }

    public int getVWidth() {
        return vWidth;
    }

    public void setVWidth(int vWidth) {
        this.vWidth = vWidth;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public Distribution getDist() {
        return dist;
    }

    public void setDist(Distribution dist) {
        this.dist = dist;
    }

    public boolean isLoaded() {
        return (flags & FL_LOADED) != 0;
    }

    public void markLoaded() {
        flags |= FL_LOADED;
    }
}
