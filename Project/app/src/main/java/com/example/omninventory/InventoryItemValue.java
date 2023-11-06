package com.example.omninventory;
import android.util.Log;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static String numToString(Long x) {
        // separate value into dollars and cents
        long dollarsNum = x / 100;
        long centsNum = x - 100 * dollarsNum;

        // using Locale.CANADA here makes a lot of assumptions, however dealing with different
        // currency formats is not within the scope of this project
        Log.d("ValueEditText", String.format("Formatting values: %d, %d", dollarsNum, centsNum));
        String formatted = String.format(Locale.CANADA, "$%d.%02d", dollarsNum, centsNum);

        return formatted;
    }

    public static Long stringToNum(String x) {
        // get rid of non digits
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

    public static String makeValidString(String x) {
        // converts a string in any format to a string in currency format
        return numToString(stringToNum(x));
    }
}
