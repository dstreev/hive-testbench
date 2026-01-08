/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds.distribution;

import org.tpcds.core.RandomNumberGenerator;
import org.tpcds.types.DsDate;
import org.tpcds.types.DsDecimal;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.*;

/**
 * Manages loading and accessing distributions from the tpcds.idx file.
 * This is a direct port of dist.c to ensure identical behavior.
 */
public class DistributionManager {

    private static final int D_NAME_LEN = 20;
    private static final int IDX_SIZE = D_NAME_LEN + 7 * 4;  // name + 7 ints

    private String distributionFile;
    private byte[] distributionData;  // For in-memory loading
    private Map<String, DistributionIndex> indexMap;
    private List<DistributionIndex> indexList;
    private boolean indexLoaded = false;
    private RandomNumberGenerator rng;

    public DistributionManager(String distributionFile, RandomNumberGenerator rng) {
        this.distributionFile = distributionFile;
        this.rng = rng;
        this.indexMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.indexList = new ArrayList<>();
    }

    /**
     * Default constructor for stream-based loading.
     */
    public DistributionManager() {
        this.indexMap = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        this.indexList = new ArrayList<>();
    }

    /**
     * Set the RNG after construction.
     */
    public void setRng(RandomNumberGenerator rng) {
        this.rng = rng;
    }

    /**
     * Load distributions from an InputStream (e.g., from classpath resource).
     */
    public synchronized void loadFromStream(InputStream is) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int read;
        while ((read = is.read(buffer)) != -1) {
            baos.write(buffer, 0, read);
        }
        this.distributionData = baos.toByteArray();
        loadIndexFromData();
    }

    /**
     * Load the distribution index from the .idx file.
     */
    public synchronized void loadIndex() throws IOException {
        if (indexLoaded) {
            return;
        }

        try (RandomAccessFile file = new RandomAccessFile(distributionFile, "r")) {
            // Read entry count from beginning
            byte[] countBytes = new byte[4];
            file.read(countBytes);
            int entryCount = ByteBuffer.wrap(countBytes).order(ByteOrder.BIG_ENDIAN).getInt();

            // Seek to index at end of file
            long indexOffset = file.length() - (long) entryCount * IDX_SIZE;
            file.seek(indexOffset);

            // Read each index entry
            for (int i = 0; i < entryCount; i++) {
                DistributionIndex di = new DistributionIndex();

                // Read name (20 bytes, null-terminated)
                byte[] nameBytes = new byte[D_NAME_LEN];
                file.read(nameBytes);
                int nameEnd = 0;
                while (nameEnd < D_NAME_LEN && nameBytes[nameEnd] != 0) {
                    nameEnd++;
                }
                di.setName(new String(nameBytes, 0, nameEnd));

                // Read index fields (7 x 4-byte big-endian ints)
                di.setIndex(readInt(file));
                di.setOffset(readInt(file));
                di.setStrSpace(readInt(file));
                di.setLength(readInt(file));
                di.setWWidth(readInt(file));
                di.setVWidth(readInt(file));
                di.setNameSpace(readInt(file));

                indexList.add(di);
                indexMap.put(di.getName(), di);
            }

            // Sort by name for binary search compatibility
            indexList.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        }

        indexLoaded = true;
    }

    private int readInt(RandomAccessFile file) throws IOException {
        byte[] bytes = new byte[4];
        file.read(bytes);
        return ByteBuffer.wrap(bytes).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    private int readIntFromData(int offset) {
        return ByteBuffer.wrap(distributionData, offset, 4).order(ByteOrder.BIG_ENDIAN).getInt();
    }

    /**
     * Load the distribution index from in-memory data.
     */
    private synchronized void loadIndexFromData() throws IOException {
        if (indexLoaded) {
            return;
        }

        // Read entry count from beginning
        int entryCount = readIntFromData(0);

        // Index is at end of data
        int indexOffset = distributionData.length - entryCount * IDX_SIZE;

        // Read each index entry
        int pos = indexOffset;
        for (int i = 0; i < entryCount; i++) {
            DistributionIndex di = new DistributionIndex();

            // Read name (20 bytes, null-terminated)
            int nameEnd = 0;
            while (nameEnd < D_NAME_LEN && distributionData[pos + nameEnd] != 0) {
                nameEnd++;
            }
            di.setName(new String(distributionData, pos, nameEnd));
            pos += D_NAME_LEN;

            // Read index fields (7 x 4-byte big-endian ints)
            di.setIndex(readIntFromData(pos)); pos += 4;
            di.setOffset(readIntFromData(pos)); pos += 4;
            di.setStrSpace(readIntFromData(pos)); pos += 4;
            di.setLength(readIntFromData(pos)); pos += 4;
            di.setWWidth(readIntFromData(pos)); pos += 4;
            di.setVWidth(readIntFromData(pos)); pos += 4;
            di.setNameSpace(readIntFromData(pos)); pos += 4;

            indexList.add(di);
            indexMap.put(di.getName(), di);
        }

        // Sort by name for binary search compatibility
        indexList.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
        indexLoaded = true;
    }

    /**
     * Load a distribution's data from in-memory data.
     */
    private synchronized void loadDistFromData(DistributionIndex di) throws IOException {
        if (di.isLoaded()) {
            return;
        }

        int pos = di.getOffset();
        Distribution d = new Distribution();
        d.setSize(di.getLength());

        // Load type vector
        int[] typeVector = new int[di.getVWidth()];
        for (int i = 0; i < di.getVWidth(); i++) {
            typeVector[i] = readIntFromData(pos); pos += 4;
        }
        d.setTypeVector(typeVector);

        // Load weight sets (and calculate cumulative weights and maximums)
        int[][] weightSets = new int[di.getWWidth()][di.getLength()];
        int[] maximums = new int[di.getWWidth()];
        for (int w = 0; w < di.getWWidth(); w++) {
            maximums[w] = 0;
            for (int j = 0; j < di.getLength(); j++) {
                int weight = readIntFromData(pos); pos += 4;
                maximums[w] += weight;
                weightSets[w][j] = maximums[w];  // Store cumulative weight
            }
        }
        d.setWeightSets(weightSets);
        d.setMaximums(maximums);

        // Load value sets (offsets into strings)
        int[][] valueSets = new int[di.getVWidth()][di.getLength()];
        for (int v = 0; v < di.getVWidth(); v++) {
            for (int j = 0; j < di.getLength(); j++) {
                valueSets[v][j] = readIntFromData(pos); pos += 4;
            }
        }
        d.setValueSets(valueSets);

        // Load column aliases if present
        if (di.getNameSpace() > 0) {
            d.setNames(new String(distributionData, pos, di.getNameSpace()));
            pos += di.getNameSpace();
        }

        // Load string values
        d.setStrings(new String(distributionData, pos, di.getStrSpace()));

        di.setDist(d);
        di.markLoaded();
    }

    /**
     * Find a distribution by name.
     */
    public DistributionIndex findDist(String name) {
        if (!indexLoaded) {
            try {
                if (distributionData != null) {
                    loadIndexFromData();
                } else {
                    loadIndex();
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load distribution index: " + e.getMessage(), e);
            }
        }

        DistributionIndex di = indexMap.get(name);
        if (di != null && !di.isLoaded()) {
            try {
                if (distributionData != null) {
                    loadDistFromData(di);
                } else {
                    loadDist(di);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to load distribution '" + name + "': " + e.getMessage(), e);
            }
        }

        return di;
    }

    /**
     * Load a distribution's data.
     */
    private synchronized void loadDist(DistributionIndex di) throws IOException {
        if (di.isLoaded()) {
            return;
        }

        try (RandomAccessFile file = new RandomAccessFile(distributionFile, "r")) {
            file.seek(di.getOffset());

            Distribution d = new Distribution();
            d.setSize(di.getLength());

            // Load type vector
            int[] typeVector = new int[di.getVWidth()];
            for (int i = 0; i < di.getVWidth(); i++) {
                typeVector[i] = readInt(file);
            }
            d.setTypeVector(typeVector);

            // Load weight sets (and calculate cumulative weights and maximums)
            int[][] weightSets = new int[di.getWWidth()][di.getLength()];
            int[] maximums = new int[di.getWWidth()];
            for (int w = 0; w < di.getWWidth(); w++) {
                maximums[w] = 0;
                for (int j = 0; j < di.getLength(); j++) {
                    int weight = readInt(file);
                    maximums[w] += weight;
                    weightSets[w][j] = maximums[w];  // Store cumulative weight
                }
            }
            d.setWeightSets(weightSets);
            d.setMaximums(maximums);

            // Load value sets (offsets into strings)
            int[][] valueSets = new int[di.getVWidth()][di.getLength()];
            for (int v = 0; v < di.getVWidth(); v++) {
                for (int j = 0; j < di.getLength(); j++) {
                    valueSets[v][j] = readInt(file);
                }
            }
            d.setValueSets(valueSets);

            // Load column aliases if present
            if (di.getNameSpace() > 0) {
                byte[] nameBytes = new byte[di.getNameSpace()];
                file.read(nameBytes);
                d.setNames(new String(nameBytes));
            }

            // Load string values
            byte[] strBytes = new byte[di.getStrSpace()];
            file.read(strBytes);
            d.setStrings(new String(strBytes));

            di.setDist(d);
            di.markLoaded();
        }
    }

    /**
     * Pick a value from a distribution using weighted random selection.
     *
     * @param distName Name of the distribution
     * @param vset Value set (1-based)
     * @param wset Weight set (1-based)
     * @param stream RNG stream to use
     * @return The selected string value
     */
    public String pickDistribution(String distName, int vset, int wset, int stream) {
        if (rng == null) {
            throw new IllegalStateException("RandomNumberGenerator not set on DistributionManager. Call setRng() first.");
        }

        DistributionIndex di = findDist(distName);
        if (di == null) {
            throw new IllegalArgumentException("Invalid distribution name: " + distName);
        }

        Distribution dist = di.getDist();

        // Generate random level in range [1, max]
        int level = rng.genrandInteger(RandomNumberGenerator.DIST_UNIFORM, 1,
                dist.getMaxWeight(wset), 0, stream);

        // Find the index where cumulative weight exceeds level
        int index = 0;
        while (level > dist.getCumulativeWeight(index, wset) && index < di.getLength()) {
            index++;
        }

        if (index >= di.getLength()) {
            throw new RuntimeException("Distribution overrun");
        }

        // Get the string value at this index
        int offset = dist.getValueOffset(index, vset);
        return dist.getStringValue(offset);
    }

    /**
     * Pick an integer value from a distribution.
     */
    public int pickDistributionInt(String distName, int vset, int wset, int stream) {
        String value = pickDistribution(distName, vset, wset, stream);
        return Integer.parseInt(value);
    }

    /**
     * Pick a date value from a distribution.
     */
    public DsDate pickDistributionDate(String distName, int vset, int wset, int stream) {
        String value = pickDistribution(distName, vset, wset, stream);
        return DsDate.fromString(value);
    }

    /**
     * Pick a decimal value from a distribution.
     */
    public DsDecimal pickDistributionDecimal(String distName, int vset, int wset, int stream) {
        String value = pickDistribution(distName, vset, wset, stream);
        return DsDecimal.fromString(value);
    }

    /**
     * Get a specific member of a distribution by index (1-based).
     */
    public String distMember(String distName, int index, int vset) {
        DistributionIndex di = findDist(distName);
        if (di == null) {
            throw new IllegalArgumentException("Invalid distribution name: " + distName);
        }

        Distribution dist = di.getDist();
        int row = index - 1;  // Convert to 0-based

        if (row < 0 || row >= di.getLength()) {
            throw new IllegalArgumentException("Distribution index out of range: " + index);
        }

        int offset = dist.getValueOffset(row, vset);
        return dist.getStringValue(offset);
    }

    /**
     * Get the weight of a specific member (1-based index).
     */
    public int distWeight(String distName, int index, int wset) {
        DistributionIndex di = findDist(distName);
        if (di == null) {
            throw new IllegalArgumentException("Invalid distribution name: " + distName);
        }

        Distribution dist = di.getDist();
        int row = index - 1;  // Convert to 0-based

        int res = dist.getCumulativeWeight(row, wset);

        // Reverse the accumulation to get individual weight
        if (row > 0) {
            res -= dist.getCumulativeWeight(row - 1, wset);
        }

        return res;
    }

    /**
     * Get the size (number of entries) in a distribution.
     */
    public int distSize(String distName) {
        DistributionIndex di = findDist(distName);
        if (di == null) {
            return -1;
        }
        return di.getLength();
    }

    /**
     * Get the type of a value set in a distribution.
     */
    public int distType(String distName, int vset) {
        DistributionIndex di = findDist(distName);
        if (di == null || vset < 1 || vset > di.getVWidth()) {
            return -1;
        }
        return di.getDist().getType(vset);
    }

    /**
     * Get the number of entries with non-zero weight in a weight set.
     */
    public int distActive(String distName, int wset) {
        int size = distSize(distName);
        int result = 0;
        for (int i = 1; i <= size; i++) {
            if (distWeight(distName, i, wset) != 0) {
                result++;
            }
        }
        return result;
    }

    /**
     * Find the row number where a value is found.
     */
    public int findDistValue(String value, String distName, int vset) {
        int size = distSize(distName);
        for (int i = 1; i <= size; i++) {
            String distValue = distMember(distName, i, vset);
            if (value.equals(distValue)) {
                return i;
            }
        }
        return -1;
    }
}
