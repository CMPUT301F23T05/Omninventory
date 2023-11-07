package com.example.omninventory;

import android.util.Log;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * In different places of the code a date (year, month, and day) may be represented as a Date, a
 * Calendar, and as a String. This contains all of those representations and allows conversion
 * between them.
 */
public class ItemDate implements Serializable {
    private Calendar cal;

    public ItemDate(Calendar x) {
        this.cal = x;
    }

    public ItemDate(String x) {
        this.cal = stringToCal(x);
    }

    public ItemDate(Date x) {
        this.cal = dateToCal(x);
    }

    @NonNull
    @Override
    public String toString() {
        return calToString(cal);
    }

    public Date toDate() {
        return calToDate(cal);
    }

    public Calendar toCalendar() {
        return cal;
    }

    /**
     * Convert a string in YYYY-MM-DD format to a Date. If string is not in valid format, no
     * guarantee on what Date will be returned.
     * @param x
     * @return
     */
    public static Calendar stringToCal(String x) {

        try {
            // regex match to YYYY-MM-DD format
            Pattern p = Pattern.compile("^(\\d{4})-(\\d{2})-(\\d{2})$");
            Matcher m = p.matcher(x);

            if (m.matches()) {
                Log.d("ItemDate", String.format("%s %s %s", m.group(1), m.group(2), m.group(3)));
                // create a calendar from year, month, and day
                int year = Integer.parseInt(m.group(1));
                int month = Integer.parseInt(m.group(2)) - 1;
                int day = Integer.parseInt(m.group(3));

                Calendar c = Calendar.getInstance();
                c.clear();
                c.set(year, month, day); // set date from string
                return c;
            }
            else {
                throw new IllegalArgumentException(String.format("Invalid string %s provided", x));
            }
        }
        catch (IllegalArgumentException e) {
            Log.e("ItemDate", "Invalid date provided to stringToDate, returning a default value.", e);

            // you get a default calendar with current date
            Calendar c = Calendar.getInstance();
            c.clear();
            return c;
        }
    }

    /**
     * Convert a Date to a Calendar.
     * @param x
     * @return
     */
    public static Calendar dateToCal(Date x) {
        Calendar c = Calendar.getInstance();
        c.setTime(x);
        return c;
    }

    /**
     * Convert a Calendar to a Date.
     * @param x
     * @return
     */
    public static Date calToDate(Calendar x) {
        return x.getTime();
    }

    /**
     * Convert a Calendar to a String in YYYY-MM-DD format.
     * @param x
     * @return
     */
    public static String calToString(Calendar x) {
        return ymdToString(x.get(Calendar.YEAR), x.get(Calendar.MONTH) + 1, x.get(Calendar.DATE));
    }

    public static String ymdToString(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // TODO: implement rest of converstion methods just for the sake of completeness?
}
