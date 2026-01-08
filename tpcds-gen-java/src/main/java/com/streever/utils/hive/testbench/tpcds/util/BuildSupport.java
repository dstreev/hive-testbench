/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.util;

import com.streever.utils.hive.testbench.tpcds.core.RandomNumberGenerator;
import com.streever.utils.hive.testbench.tpcds.distribution.DistributionManager;

/**
 * Utility functions for building table data.
 * Port of build_support.c and address.c
 */
public class BuildSupport {

    private static final String XLATE = "ABCDEFGHIJKLMNOP";

    private final RandomNumberGenerator rng;
    private final DistributionManager distMgr;

    public BuildSupport(RandomNumberGenerator rng, DistributionManager distMgr) {
        this.rng = rng;
        this.distMgr = distMgr;
    }

    /**
     * Generate a business key from a primary key.
     * Converts a 64-bit key to a 16-character string.
     */
    public String mkBkey(long primary) {
        StringBuilder sb = new StringBuilder(16);

        // Upper 32 bits
        long upper = (primary >> 32) & 0xFFFFFFFFL;
        ltoc(sb, upper);

        // Lower 32 bits
        long lower = primary & 0xFFFFFFFFL;
        ltoc(sb, lower);

        return sb.toString();
    }

    private void ltoc(StringBuilder sb, long val) {
        for (int i = 0; i < 8; i++) {
            char c = XLATE.charAt((int)(val & 0xF));
            sb.append(c);
            val >>= 4;
        }
    }

    /**
     * Generate a word from syllables.
     */
    public String mkWord(String sylSet, long src, int maxChars) {
        StringBuilder dest = new StringBuilder();
        long i = src;

        while (i > 0) {
            int syllableCount = distMgr.distSize(sylSet);
            String syllable = distMgr.distMember(sylSet, (int)(i % syllableCount) + 1, 1);
            i /= syllableCount;

            if (dest.length() + syllable.length() <= maxChars) {
                dest.append(syllable);
            } else {
                break;
            }
        }

        return dest.toString();
    }

    /**
     * Generate a company name from syllables.
     */
    public String mkCompanyName(int company) {
        return mkWord("syllables", company, 10);
    }

    /**
     * Generate an address.
     */
    public Address mkAddress(int column) {
        Address addr = new Address();

        // Use column modulo to avoid stream overflow
        int stream = column % 100;

        // Street number [1..1000]
        addr.streetNum = rng.genrandInteger(RandomNumberGenerator.DIST_UNIFORM, 1, 1000, 0, stream);

        // Street names from distribution
        addr.streetName1 = distMgr.pickDistribution("street_names", 1, 1, stream);
        addr.streetName2 = distMgr.pickDistribution("street_names", 1, 2, stream + 1);

        // Street type from distribution
        addr.streetType = distMgr.pickDistribution("street_type", 1, 1, stream + 2);

        // Suite number - alphabetic 50% of time
        int i = rng.genrandInteger(RandomNumberGenerator.DIST_UNIFORM, 1, 100, 0, stream + 3);
        if ((i & 0x01) != 0) {
            addr.suiteNum = "Suite " + ((i >> 1) * 10);
        } else {
            addr.suiteNum = "Suite " + (char)(((i >> 1) % 25) + 'A');
        }

        // City from distribution
        addr.city = distMgr.pickDistribution("cities", 1, 1, stream + 4);

        // County/state from fips_county distribution - pick a random row
        int fipsSize = distMgr.distSize("fips_county");
        int region = (column % fipsSize) + 1;  // 1-based index
        addr.county = distMgr.distMember("fips_county", region, 2);
        addr.state = distMgr.distMember("fips_county", region, 3);

        // Zip code based on city hash
        addr.zip = cityHash(addr.city);
        try {
            String zipPrefix = distMgr.distMember("fips_county", region, 5);
            if (zipPrefix != null && !zipPrefix.isEmpty() && Character.isDigit(zipPrefix.charAt(0))) {
                if (zipPrefix.charAt(0) == '0' && addr.zip < 9400) {
                    addr.zip += 600;
                }
                addr.zip += (zipPrefix.charAt(0) - '0') * 10000;
            }
        } catch (Exception e) {
            // Use default zip if lookup fails
            addr.zip += 10000;
        }

        // Plus4 from hash of full address
        String fullAddr = addr.streetNum + " " + addr.streetName1 + " " +
            (addr.streetName2 != null ? addr.streetName2 : "") + " " + addr.streetType;
        addr.plus4 = cityHash(fullAddr);

        // GMT offset and country
        try {
            String gmtStr = distMgr.distMember("fips_county", region, 6);
            addr.gmtOffset = Integer.parseInt(gmtStr);
        } catch (Exception e) {
            addr.gmtOffset = -5;  // Default to EST
        }
        addr.country = "United States";

        return addr;
    }

    /**
     * Hash a city name to generate zip code portion.
     */
    public int cityHash(String name) {
        int hashValue = 0;
        int res = 0;

        for (char c : name.toCharArray()) {
            hashValue *= 26;
            hashValue -= 'A';
            hashValue += c;
            if (hashValue > 1000000) {
                hashValue %= 10000;
                res += hashValue;
                hashValue = 0;
            }
        }
        hashValue %= 1000;
        res += hashValue;
        res %= 10000;

        return res;
    }

    /**
     * Generate random text of specified length.
     */
    public String genText(int minLen, int maxLen, int column) {
        int len = rng.genrandInteger(RandomNumberGenerator.DIST_UNIFORM, minLen, maxLen, 0, column);
        return rng.genCharset(RandomNumberGenerator.ALPHANUM, len, len, column);
    }

    /**
     * Select from a distribution based on a bitmap key.
     */
    public String bitmapToDist(String distName, long[] modulus, int vset) {
        int size = distMgr.distSize(distName);
        int m = (int)((modulus[0] % size) + 1);
        modulus[0] /= size;
        return distMgr.distMember(distName, m, vset);
    }

    /**
     * Embed a string from a distribution into another string at a random position.
     */
    public String embedString(String dest, String distName, int vset, int wset, int stream) {
        String word = distMgr.pickDistribution(distName, vset, wset, stream);
        if (dest.length() <= word.length()) {
            return word;
        }
        int position = rng.genrandInteger(RandomNumberGenerator.DIST_UNIFORM, 0,
            dest.length() - word.length() - 1, 0, stream);
        StringBuilder sb = new StringBuilder(dest);
        sb.replace(position, position + word.length(), word);
        return sb.toString();
    }
}
