/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.types;

/**
 * Date type using Julian day representation.
 * This is a direct port of the TPC-DS date.c to ensure identical output.
 *
 * Julian day conversion uses Fleigel and Van Flandern algorithm (CACM, vol 11, #10, Oct. 1968, p. 657)
 */
public class DsDate {

    public static final int OP_FIRST_DOM = 0x01;  // First day of month
    public static final int OP_LAST_DOM = 0x02;   // Last day of month
    public static final int OP_SAME_LY = 0x03;    // Same day last year
    public static final int OP_SAME_LQ = 0x04;    // Same offset in prior quarter

    public static final String[] WEEKDAY_NAMES = {
        null, "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"
    };

    private static final int[][] M_DAYS = {
        {0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334},
        {0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335}
    };

    private static final String[] QTR_START = {null, "01-01", "04-01", "07-01", "10-01"};

    private int flags;
    private int year;
    private int month;
    private int day;
    private int julian;

    public DsDate() {
        this.flags = 0;
        this.year = 0;
        this.month = 0;
        this.day = 0;
        this.julian = 0;
    }

    public DsDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
        this.julian = computeJulian(year, month, day);
        this.flags = 0;
    }

    /**
     * Create a date from a Julian day number.
     * Uses Fleigel and Van Flandern algorithm.
     */
    public static DsDate fromJulian(int julian) {
        if (julian < 0) {
            return null;
        }

        DsDate d = new DsDate();
        d.julian = julian;

        long l = julian + 68569L;
        long n = (4 * l) / 146097;
        l = l - (146097 * n + 3) / 4;
        long i = (4000 * (l + 1)) / 1461001;
        l = l - (1461 * i) / 4 + 31;
        long j = (80 * l) / 2447;
        d.day = (int) (l - (2447 * j) / 80);
        l = j / 11;
        d.month = (int) (j + 2 - 12 * l);
        d.year = (int) (100 * (n - 49) + i + l);

        return d;
    }

    /**
     * Parse a date from string format YYYY-MM-DD.
     */
    public static DsDate fromString(String str) {
        if (str == null || str.isEmpty()) {
            return null;
        }

        String[] parts = str.split("-");
        if (parts.length != 3) {
            throw new IllegalArgumentException("Invalid date format: " + str);
        }

        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int day = Integer.parseInt(parts[2]);

        return new DsDate(year, month, day);
    }

    /**
     * Convert date to Julian day number.
     */
    public int toJulian() {
        return julian;
    }

    /**
     * Compute Julian day from year/month/day.
     */
    private static int computeJulian(int year, int month, int day) {
        int y = year;
        int m = month;

        if (m <= 2) {
            m += 12;
            y -= 1;
        }

        // Formula from http://quasar.as.utexas.edu/BillInfo/JulianDatesG.html
        // Added 1 to match jtodt/dttoj in C code
        return day + (153 * m - 457) / 5 + 365 * y + y / 4 - y / 100 + y / 400 + 1721118 + 1;
    }

    /**
     * Check if a year is a leap year.
     */
    public static boolean isLeap(int year) {
        if (year % 100 == 0) {
            return (year % 400) % 2 == 0;
        }
        return year % 4 == 0;
    }

    /**
     * Get day number in year (for calendar distribution lookup).
     */
    public int dayNumber() {
        return M_DAYS[isLeap(year) ? 1 : 0][month] + day;
    }

    /**
     * Get day of week (0=Sunday, 1=Monday, ..., 6=Saturday).
     */
    public int dayOfWeek() {
        int[] doomsday = {3, 2, 0, 5};
        int[] known = {0, 3, 0, 0, 4, 9, 6, 11, 8, 5, 10, 7, 12};

        // Adjust known dates for leap years
        if (isLeap(year)) {
            known[1] = 4;
            known[2] = 1;
        } else {
            known[1] = 3;
            known[2] = 0;
        }

        // Calculate doomsday for the century
        int dday = year / 100;
        dday -= 15;
        dday %= 4;
        dday = doomsday[dday];

        // Calculate doomsday for the year
        int q = year % 100;
        int r = q % 12;
        q /= 12;
        int s = r / 4;
        dday += q + r + s;
        dday %= 7;

        int res = day - known[month];
        while (res < 0) res += 7;
        while (res > 6) res -= 7;

        res += dday;
        res %= 7;

        return res;
    }

    /**
     * Perform date operations.
     */
    public DsDate operate(int op, DsDate other) {
        switch (op) {
            case OP_FIRST_DOM:
                return DsDate.fromJulian(julian - day + 1);

            case OP_LAST_DOM:
                int leapIdx = isLeap(year) ? 1 : 0;
                return DsDate.fromJulian(julian - day + M_DAYS[leapIdx][month]);

            case OP_SAME_LY:
                if (isLeap(year) && month == 2 && day == 29) {
                    return new DsDate(year - 1, 2, 28);
                }
                return new DsDate(year - 1, month, day);

            case OP_SAME_LQ:
                return computeSameLastQuarter();

            default:
                throw new IllegalArgumentException("Invalid date operation: " + op);
        }
    }

    private DsDate computeSameLastQuarter() {
        int qtr;
        if (month <= 3) qtr = 1;
        else if (month <= 6) qtr = 2;
        else if (month <= 9) qtr = 3;
        else qtr = 4;

        DsDate qtrStart = DsDate.fromString(year + "-" + QTR_START[qtr]);
        int offset = julian - qtrStart.julian;

        int prevQtr = (qtr == 1) ? 4 : qtr - 1;
        int prevYear = (qtr == 1) ? year - 1 : year;
        DsDate prevQtrStart = DsDate.fromString(prevYear + "-" + QTR_START[prevQtr]);

        return DsDate.fromJulian(prevQtrStart.julian + offset);
    }

    /**
     * Parse time string to seconds since midnight.
     */
    public static int parseTime(String str) {
        String[] parts = str.split(":");
        int hour, min, sec = 0;

        if (parts.length == 3) {
            hour = Integer.parseInt(parts[0]);
            min = Integer.parseInt(parts[1]);
            sec = Integer.parseInt(parts[2]);
        } else if (parts.length == 2) {
            hour = Integer.parseInt(parts[0]);
            min = Integer.parseInt(parts[1]);
        } else {
            throw new IllegalArgumentException("Invalid time format: " + str);
        }

        if (hour < 0 || hour > 23 || min < 0 || min > 59 || sec < 0 || sec > 59) {
            throw new IllegalArgumentException("Invalid time format: " + str);
        }

        return hour * 3600 + min * 60 + sec;
    }

    @Override
    public String toString() {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // Getters
    public int getFlags() { return flags; }
    public int getYear() { return year; }
    public int getMonth() { return month; }
    public int getDay() { return day; }
    public int getJulian() { return julian; }

    // Setters
    public void setFlags(int flags) { this.flags = flags; }
}
