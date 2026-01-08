/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package org.tpcds.core;

import org.tpcds.types.DsDecimal;
import org.tpcds.types.DsDate;

/**
 * Random Number Generator using Linear Congruential Generator (LCG) algorithm.
 * This is a direct port of the TPC-DS genrand.c to ensure identical output.
 *
 * The LCG uses:
 * - Multiplier: 16807
 * - Modulus: 2147483647 (MAXINT, 2^31 - 1)
 * - Default seed: 19620718
 */
public class RandomNumberGenerator {

    public static final int DIST_UNIFORM = 0;
    public static final int DIST_EXPONENTIAL = 1;
    public static final int DIST_SALES = 2;
    public static final int DIST_RETURNS = 3;

    public static final long RNG_SEED = 19620718L;
    public static final long MAXINT = 2147483647L;  // INT_MAX (2^31 - 1)
    public static final int MAX_COLUMN = 799;

    private static final long MULT = 16807L;
    private static final long NQ = 127773L;   // MAXINT / MULT
    private static final long NR = 2836L;     // MAXINT % MULT

    public static final String ALPHANUM = "abcdefghijklmnopqrstuvxyzABCDEFGHIJKLMNOPQRSTUVXYZ0123456789";
    public static final String DIGITS = "0123456789";

    private final RandomNumberStream[] streams;
    private boolean initialized = false;
    private Long customSeed = null;

    public RandomNumberGenerator() {
        this.streams = new RandomNumberStream[MAX_COLUMN + 1];
    }

    /**
     * Initialize all random number streams.
     * Each column gets its own stream with a unique initial seed.
     */
    public void init() {
        if (initialized) {
            return;
        }

        long seed = (customSeed != null) ? customSeed : RNG_SEED;
        long skip = MAXINT / MAX_COLUMN;

        for (int i = 0; i <= MAX_COLUMN; i++) {
            long streamSeed = seed + skip * i;
            streams[i] = new RandomNumberStream(i, 0, 0, streamSeed);
        }

        initialized = true;
    }

    /**
     * Set a custom RNG seed (must be called before init).
     */
    public void setSeed(long seed) {
        this.customSeed = seed;
    }

    /**
     * Generate the next random number in the specified stream.
     * Uses the Linear Congruential Generator algorithm.
     *
     * @param stream The stream index to use
     * @return A random long in range [0, MAXINT)
     */
    public long nextRandom(int stream) {
        ensureInitialized();

        long s = streams[stream].getSeed();
        long divRes = s / NQ;
        long modRes = s - NQ * divRes;  // s % NQ
        s = MULT * modRes - divRes * NR;

        if (s < 0) {
            s += MAXINT;
        }

        streams[stream].setSeed(s);
        streams[stream].incrementUsed();

        return s;
    }

    /**
     * Generate a random double in range [0, 1).
     */
    public double nextRandomFloat(int stream) {
        long res = nextRandom(stream);
        return (double) res / (double) MAXINT;
    }

    /**
     * Skip ahead N positions in a stream using fast exponentiation.
     * This allows parallel generation by skipping to different positions.
     *
     * @param stream The stream to skip
     * @param n Number of positions to skip
     */
    public void skipRandom(int stream, long n) {
        ensureInitialized();

        long m = MULT;
        long z = streams[stream].getInitialSeed();

        while (n > 0) {
            if (n % 2 != 0) {
                z = (m * z) % MAXINT;
            }
            n = n / 2;
            m = (m * m) % MAXINT;
        }

        streams[stream].setSeed(z);
    }

    /**
     * Generate a random integer within a range using the specified distribution.
     */
    public int genrandInteger(int dist, int min, int max, int mean, int stream) {
        int res;

        switch (dist) {
            case DIST_UNIFORM:
                res = (int) nextRandom(stream);
                res = res % (max - min + 1);
                res += min;
                break;

            case DIST_EXPONENTIAL:
                double fres = 0;
                for (int i = 0; i < 12; i++) {
                    fres += ((double) nextRandom(stream) / MAXINT) - 0.5;
                }
                res = min + (int) ((max - min + 1) * fres);
                break;

            default:
                throw new IllegalArgumentException("Undefined distribution: " + dist);
        }

        return res;
    }

    /**
     * Generate a random long key within a range.
     */
    public long genrandKey(int dist, long min, long max, long mean, int stream) {
        long res;

        switch (dist) {
            case DIST_UNIFORM:
                res = nextRandom(stream);
                res = res % (max - min + 1);
                res += min;
                break;

            case DIST_EXPONENTIAL:
                double fres = 0;
                for (int i = 0; i < 12; i++) {
                    fres += ((double) nextRandom(stream) / MAXINT) - 0.5;
                }
                res = min + (long) ((max - min + 1) * fres);
                break;

            default:
                throw new IllegalArgumentException("Undefined distribution: " + dist);
        }

        return res;
    }

    /**
     * Generate a random decimal value within a range.
     */
    public DsDecimal genrandDecimal(int dist, DsDecimal min, DsDecimal max, DsDecimal mean, int stream) {
        int precision = Math.min(min.getPrecision(), max.getPrecision());
        long number;

        switch (dist) {
            case DIST_UNIFORM:
                number = nextRandom(stream);
                number = number % (max.getNumber() - min.getNumber() + 1);
                number += min.getNumber();
                break;

            case DIST_EXPONENTIAL:
                double fres = 0;
                for (int i = 0; i < 12; i++) {
                    fres /= 2.0;
                    fres += ((double) nextRandom(stream) / (double) MAXINT) - 0.5;
                }
                number = mean.getNumber() + (long) ((max.getNumber() - min.getNumber() + 1) * fres);
                break;

            default:
                throw new IllegalArgumentException("Undefined distribution: " + dist);
        }

        // Calculate scale
        int scale = 0;
        long temp = number;
        while (temp > 10) {
            temp /= 10;
            scale++;
        }

        return new DsDecimal(number, precision, scale);
    }

    /**
     * Generate a random date within a range.
     */
    public DsDate genrandDate(int dist, DsDate min, DsDate max, DsDate mean, int stream) {
        int minJulian = min.toJulian();
        int range = max.toJulian() - minJulian;
        int imean = 0;
        int result;

        switch (dist) {
            case DIST_EXPONENTIAL:
                imean = mean.toJulian() - minJulian;
                // fall through
            case DIST_UNIFORM:
                int temp = genrandInteger(dist, 0, range, imean, stream);
                result = minJulian + temp;
                break;

            case DIST_SALES:
            case DIST_RETURNS:
                // These distributions require calendar weight data
                // For now, use uniform distribution as fallback
                temp = genrandInteger(DIST_UNIFORM, 0, range, 0, stream);
                result = minJulian + temp;
                break;

            default:
                throw new IllegalArgumentException("Undefined distribution: " + dist);
        }

        return DsDate.fromJulian(result);
    }

    /**
     * Generate a random string from a character set.
     */
    public String genCharset(String set, int min, int max, int stream) {
        if (set == null || set.isEmpty()) {
            return "";
        }

        int len = genrandInteger(DIST_UNIFORM, min, max, 0, stream);
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < max; i++) {
            int idx = genrandInteger(DIST_UNIFORM, 0, set.length() - 1, 0, stream);
            if (i < len) {
                sb.append(set.charAt(idx));
            }
        }

        return sb.toString();
    }

    /**
     * Generate a random email address.
     */
    public String genrandEmail(String firstName, String lastName, int column) {
        String domain = "foo.com"; // TODO: use distribution when available
        int companyLength = genrandInteger(DIST_UNIFORM, 10, 20, 0, column);
        String company = genCharset(ALPHANUM, 1, 20, column);
        if (company.length() > companyLength) {
            company = company.substring(0, companyLength);
        }

        return String.format("%s.%s@%s.%s", firstName, lastName, company, domain);
    }

    /**
     * Generate a random IP address.
     */
    public String genrandIpAddr(int column) {
        int[] quads = new int[4];
        for (int i = 0; i < 4; i++) {
            quads[i] = genrandInteger(DIST_UNIFORM, 1, 255, 0, column);
        }
        return String.format("%03d.%03d.%03d.%03d", quads[0], quads[1], quads[2], quads[3]);
    }

    /**
     * Generate a random URL.
     */
    public String genrandUrl(int column) {
        return "http://www.foo.com";
    }

    /**
     * Reset all streams for a specific table.
     */
    public void resetSeeds(int table) {
        ensureInitialized();
        for (int i = 0; i <= MAX_COLUMN; i++) {
            if (streams[i].getTable() == table) {
                streams[i].reset();
            }
        }
    }

    /**
     * Reset a specific stream by index.
     */
    public void resetStream(int stream) {
        ensureInitialized();
        streams[stream].reset();
    }

    /**
     * Get a stream by index.
     */
    public RandomNumberStream getStream(int index) {
        ensureInitialized();
        return streams[index];
    }

    /**
     * Set a stream's seed directly.
     */
    public long setStreamSeed(int stream, long value) {
        ensureInitialized();
        long oldValue = streams[stream].getSeed();
        streams[stream].setSeed(value);
        return oldValue;
    }

    private void ensureInitialized() {
        if (!initialized) {
            init();
        }
    }
}
