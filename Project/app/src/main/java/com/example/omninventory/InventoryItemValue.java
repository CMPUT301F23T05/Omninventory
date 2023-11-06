package com.example.omninventory;
import android.util.Log;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;

/**
 * A standard representation of the 'Value' field of an InventoryItem. Stores a Long value of cents
 * representing a currency value, and has methods to convert back and forth between `String` and
 * `Long` representation.
 * Note that in Firebase, these values are stored as `long`s.
 */
public class InventoryItemValue implements Serializable {

    private Long value; // the number of cents in the currency value

    public InventoryItemValue(String x) {
        this.value = stringToNum(x);
    }

    public InventoryItemValue(long x) {
        this.value = x;
    }

    public long toPrimitiveLong() {
        return value;
    }

    @NonNull
    @Override
    public String toString() {
        return numToString(value);
    }

    /**
     * Convert a Long value of cents to a String representation of a currency value.
     * @param x The Long value of cents.
     * @return A String representing the currency value.
     */
    public static String numToString(Long x) {
        // error checking
        if (x < 0) {
            Log.d("InventoryItemValue", String.format("Converting negative value %d to $0.", x));
            x = 0L;
        }

        // separate value into dollars and cents
        long dollarsNum = x / 100;
        long centsNum = x - 100 * dollarsNum;

        // using Locale.CANADA here makes a lot of assumptions, however dealing with different
        // currency formats is not within the scope of this project
        Log.d("ValueEditText", String.format("Formatting values: %d, %d", dollarsNum, centsNum));

        return String.format(Locale.CANADA, "$%d.%02d", dollarsNum, centsNum);
    }

    /**
     * Convert a String representing a currency value to a Long number of cents. This method should
     * never throw an error for invalid input; it strictly ignores any non-digit characters in the
     * String.
     * Examples:
     *     "$1.23" -> 123L (represents $1.23)
     *     "012A34" -> 1234L (represents $12.34)
     *     "123xxxxxxx45" -> 12345L (represents $123.45)
     *
     * @param x The String representing a currency value.
     * @return A Long representing the equivalent number of cents.
     */
    public static Long stringToNum(String x) {
        // get rid of non digit characters
        String digits = x.replaceAll("[\\D]", "");

        // consider all digits entered to be the number of cents
        Long centsNum;
        try {
            if (digits.equals("")) {
                // empty or invalid string gets a value of 0
                centsNum = 0L;
            } else {
                centsNum = Long.parseLong(digits);
            }
        } catch (NumberFormatException e) {
            // long too long :/
            centsNum = Long.MAX_VALUE;
        }

        return centsNum;
    }

    /**
     * Converts a string in any format to a string in currency format.
     * @param x Unformatted string.
     * @return String representing a currency amount.
     */
    public static String makeValidString(String x) {
        return numToString(stringToNum(x));
    }
}
