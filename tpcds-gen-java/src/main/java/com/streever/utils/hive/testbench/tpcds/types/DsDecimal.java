/*
 * TPC-DS Data Generator - Java Port
 * Based on TPC-DS Benchmark specification
 */
package com.streever.utils.hive.testbench.tpcds.types;

/**
 * Decimal type using scaled integer arithmetic.
 * This is a direct port of the TPC-DS decimal.c to ensure identical output.
 */
public class DsDecimal {

    public static final int OP_PLUS = 1;
    public static final int OP_MINUS = 2;
    public static final int OP_MULT = 3;
    public static final int OP_DIV = 4;

    private static final int FL_INIT = 0x0004;

    private int flags;
    private int precision;  // Number of decimal places (fraction)
    private int scale;      // Total number of digits
    private long number;    // Scaled integer value

    public DsDecimal() {
        this.flags = 0;
        this.precision = 0;
        this.scale = 0;
        this.number = 0;
    }

    public DsDecimal(long number, int precision, int scale) {
        this.number = number;
        this.precision = precision;
        this.scale = scale;
        this.flags = FL_INIT;
    }

    /**
     * Create a decimal with specified scale and precision.
     */
    public static DsDecimal create(int scale, int precision) {
        if (scale < 0 || precision < 0) {
            return null;
        }
        DsDecimal d = new DsDecimal();
        d.scale = scale;
        d.precision = precision;
        d.flags = FL_INIT;
        return d;
    }

    /**
     * Set precision and scale, reset number to 0.
     */
    public void setPrecision(int scale, int precision) {
        this.scale = scale;
        this.precision = precision;
        this.number = 0;
        this.flags = 0;
    }

    /**
     * Convert an integer to a decimal.
     */
    public static DsDecimal fromInt(int value) {
        DsDecimal d = new DsDecimal();
        int scale = 1;
        int bound = 1;

        while ((bound * 10) <= Math.abs(value)) {
            scale++;
            bound *= 10;
        }

        d.precision = 0;
        d.scale = scale;
        d.number = value;

        return d;
    }

    /**
     * Convert a double to a decimal.
     */
    public static DsDecimal fromDouble(double value) {
        return fromString(String.format("%f", value));
    }

    /**
     * Parse a decimal from string format (e.g., "123.45" or "123").
     */
    public static DsDecimal fromString(String s) {
        if (s == null || s.isEmpty()) {
            return null;
        }

        DsDecimal d = new DsDecimal();
        d.flags = 0;

        int dotIndex = s.indexOf('.');
        if (dotIndex < 0) {
            // No decimal point
            d.scale = s.startsWith("-") ? s.length() - 1 : s.length();
            d.number = Long.parseLong(s);
            d.precision = 0;
        } else {
            String intPart = s.substring(0, dotIndex);
            String fracPart = s.substring(dotIndex + 1);

            // Remove trailing zeros from fraction part for parsing
            while (fracPart.endsWith("0") && fracPart.length() > 1) {
                fracPart = fracPart.substring(0, fracPart.length() - 1);
            }

            d.scale = intPart.startsWith("-") ? intPart.length() - 1 : intPart.length();
            long intValue = intPart.isEmpty() || intPart.equals("-") ? 0 : Long.parseLong(intPart);
            d.precision = fracPart.length();

            // Scale up integer part
            d.number = intValue;
            for (int i = 0; i < d.precision; i++) {
                d.number *= 10;
            }

            // Add fraction part
            long fracValue = fracPart.isEmpty() ? 0 : Long.parseLong(fracPart);
            if (intValue < 0 || s.startsWith("-")) {
                d.number -= fracValue;
            } else {
                d.number += fracValue;
            }
        }

        // Handle negative zero case
        if (s.startsWith("-") && d.number > 0) {
            d.number *= -1;
        }

        return d;
    }

    /**
     * Convert to string representation.
     */
    @Override
    public String toString() {
        if (precision == 0) {
            return Long.toString(number);
        }

        long wholePart = number;
        for (int i = 0; i < precision; i++) {
            wholePart /= 10;
        }

        long fracPart = Math.abs(number - wholePart * (long) Math.pow(10, precision));

        return String.format("%d.%0" + precision + "d", wholePart, fracPart);
    }

    /**
     * Convert to double.
     */
    public double toDouble() {
        double result = number;
        int p = precision;
        while (p-- > 0) {
            result /= 10.0;
        }
        return result;
    }

    /**
     * Convert to int (truncates).
     */
    public int toInt() {
        long result = number;
        for (int i = 0; i < precision; i++) {
            result /= 10;
        }
        return (int) result;
    }

    /**
     * Perform binary operation on two decimals.
     */
    public static DsDecimal operate(int op, DsDecimal d1, DsDecimal d2) {
        if (d1 == null || d2 == null) {
            return null;
        }

        DsDecimal dest = new DsDecimal();
        dest.scale = Math.max(d1.scale, d2.scale);
        dest.precision = Math.max(d1.precision, d2.precision);

        switch (op) {
            case OP_PLUS:
                dest.number = d1.number + d2.number;
                break;

            case OP_MINUS:
                dest.number = d1.number - d2.number;
                break;

            case OP_MULT:
                int res = d1.precision + d2.precision;
                dest.number = d1.number * d2.number;
                while (res-- > dest.precision) {
                    dest.number /= 10;
                }
                break;

            case OP_DIV:
                double f1 = d1.number;
                int np = d1.precision;
                while (np < dest.precision) {
                    f1 *= 10.0;
                    np++;
                }
                np = 0;
                while (np < dest.precision) {
                    f1 *= 10.0;
                    np++;
                }
                double f2 = d2.number;
                np = d2.precision;
                while (np < dest.precision) {
                    f2 *= 10.0;
                    np++;
                }
                dest.number = (long) (f1 / f2);
                break;

            default:
                throw new IllegalArgumentException("Unsupported operation: " + op);
        }

        return dest;
    }

    /**
     * Add another decimal to this one.
     */
    public DsDecimal add(DsDecimal other) {
        return operate(OP_PLUS, this, other);
    }

    /**
     * Subtract another decimal from this one.
     */
    public DsDecimal subtract(DsDecimal other) {
        return operate(OP_MINUS, this, other);
    }

    /**
     * Multiply by another decimal.
     */
    public DsDecimal multiply(DsDecimal other) {
        return operate(OP_MULT, this, other);
    }

    /**
     * Divide by another decimal.
     */
    public DsDecimal divide(DsDecimal other) {
        return operate(OP_DIV, this, other);
    }

    /**
     * Negate this decimal.
     */
    public DsDecimal negate() {
        DsDecimal d = new DsDecimal(this.number * -1, this.precision, this.scale);
        d.flags = this.flags;
        return d;
    }

    // Getters
    public int getFlags() { return flags; }
    public int getPrecision() { return precision; }
    public int getScale() { return scale; }
    public long getNumber() { return number; }

    // Setters
    public void setNumber(long number) { this.number = number; }
}
