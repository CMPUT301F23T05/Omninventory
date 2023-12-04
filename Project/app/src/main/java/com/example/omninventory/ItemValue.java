package com.example.omninventory;
import android.util.Log;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

/**
 * A standard representation of the 'Value' field of an InventoryItem. In different places of the
 * code, currency value may be represented as a numeric value or as a String. This class stores a
 * Long value of cents representing a currency value, and has methods to convert back and forth
 * between String and Long representation (which come in handy for input validation).
 *
 * Many of the methods here are small and fairly redundant. They are implemented to make this class
 * as convenient a tool for standardization and conversion as possible in the rest of the app.
 *
 * @author Castor
 */
public class ItemValue implements Serializable {

    private Long value; // the number of cents in the currency value

    /**
     * Constructor that creates an ItemValue from a String representing a currency value. Performs
     * strict input validation using ItemValue.stringToNum().
     * @param x
     */
    public ItemValue(String x) {
        this.value = stringToNum(x);
    }

    /**
     * Constructor that creates an ItemValue from a long representing the number of cents.
     * @param x The long object.
     */
    public ItemValue(long x) {
        this.value = x;
    }

    /**
     * Constructor that creates an ItemValue from a Long representing the number of cents. Uses 0L
     * as the value if the Long given is a null pointer.
     * @param x The Long object.
     */
    public ItemValue(Long x) {
        if (x != null) {
            this.value = x;
        }
        else {
            this.value = 0L;
        }
    }

    /**
     * Converts this ItemValue to a primitive long. Useful when an ItemValue must be stored in
     * Firestore.
     * @return A primitive long equivalent to this ItemValue.
     */
    public long toPrimitiveLong() {
        return value;
    }

    /**
     * Creates a String equivalent to this ItemValue.
     * @return The equivalent String.
     */
    @NonNull
    @Override
    public String toString() {
        return numToString(value);
    }

    /**
     * Compares two ItemValue objects, and returns 0 if same value.
     * If this object value greater than ie's value, return 1.
     * If this object's value less than ie's value, return -1.
     * @param ie The ItemVlue object to compare to
     * @return 1 if value greater than ie.value, 0 if value == ie.value, -1 if value less than ie.value
     */
    public int compare(ItemValue ie) {
        if (value.equals(ie.value))
            return 0;
        else if (value > ie.value)
            return 1;
        else
            return -1;
    }

    /**
     * Convert a Long value of cents to a String representation of a currency value.
     * @param x The Long value of cents.
     * @return  A String representing the currency value.
     */
    public static String numToString(Long x) {
        // error checking
        if (x < 0) {
//            Log.d("InventoryItemValue", String.format("Converting negative value %d to $0.", x));
            x = 0L;
        }

        // separate value into dollars and cents
        long dollarsNum = x / 100;
        long centsNum = x - 100 * dollarsNum;

        // using Locale.CANADA here makes a lot of assumptions, however dealing with different
        // currency formats is not within the scope of this project
//        Log.d("InventoryItemValue", String.format("Formatting values: %d, %d", dollarsNum, centsNum));

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
     * @return  A Long representing the equivalent number of cents.
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
     * @return  String representing a currency amount.
     */
    public static String makeValidString(String x) {
        return numToString(stringToNum(x));
    }
}
