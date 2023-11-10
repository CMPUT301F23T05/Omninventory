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
 * Calendar, and as a String. This class contains a single date (internally a Calendar object)
 * and allows conversion to and between all other relevant Date types.
 *
 * Many of the methods here are small and fairly redundant. They are implemented to make this class
 * as convenient a tool for standardization and conversion as possible in the rest of the app.
 *
 * @author Castor
 */
public class ItemDate implements Serializable {
    private Calendar cal;

    /**
     * Constructor that creates an ItemDate from a Calendar object.
     * @param x The Calendar object.
     */
    public ItemDate(Calendar x) {
        this.cal = x;
    }

    /**
     * Constructor that creates an ItemDate from a String object.
     * @param x The String object.
     */
    public ItemDate(String x) {
        this.cal = stringToCal(x);
    }

    /**
     * Constructor that creates an ItemDate from a Date object.
     * @param x The Date object.
     */
    public ItemDate(Date x) {
        this.cal = dateToCal(x);
    }

    /**
     * Creates a String equivalent to this ItemDate.
     * @return The equivalent String.
     */
    @NonNull
    @Override
    public String toString() {
        return calToString(cal);
    }

    /**
     * Creates a Date equivalent to this ItemDate.
     * @return The equivalent Date.
     */
    public Date toDate() {
        return calToDate(cal);
    }

    /**
     * Creates a Calendar equivalent to this ItemDate.
     * @return The equivalent Calendar.
     */
    public Calendar toCalendar() {
        return cal;
    }

    /**
     * Convert a string in YYYY-MM-DD format to a Calendar. If string is not in valid format, the
     * current date is returned.
     * @param x String that should be in YYYY-MM-DD format.
     * @return  An equivalent Calendar object.
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
     * @param x Date object to convert.
     * @return  An equivalent Calendar object.
     */
    public static Calendar dateToCal(Date x) {
        Calendar c = Calendar.getInstance();
        c.setTime(x);
        return c;
    }

    /**
     * Convert a Calendar to a Date.
     * @param x Calendar object to convert.
     * @return  An equivalent Calendar object.
     */
    public static Date calToDate(Calendar x) {
        return x.getTime();
    }

    /**
     * Convert a Calendar to a String in YYYY-MM-DD format.
     * @param x Calendar object to convert.
     * @return  An equivalent String object.
     */
    public static String calToString(Calendar x) {
        return ymdToString(x.get(Calendar.YEAR), x.get(Calendar.MONTH) + 1, x.get(Calendar.DATE));
    }

    /**
     * Create a String in YYYY-MM-DD format from three integers representing the year, month, and day.
     * @param year   The year of the date.
     * @param month  The month of the date.
     * @param day    The day of the date.
     * @return       A String representing the date.
     */
    public static String ymdToString(int year, int month, int day) {
        return String.format("%04d-%02d-%02d", year, month, day);
    }

    // TODO: implement rest of conversion methods just for the sake of completeness?
}
