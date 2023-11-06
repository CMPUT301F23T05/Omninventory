package com.example.omninventory;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * With help from:
 * https://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext
 */
public class ValueTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public ValueTextWatcher(EditText editText) {
        // initialize with reference to EditText to watch
        editTextWeakReference = new WeakReference<EditText>(editText);

        // make an empty Editable to mock an empty EditText and set EditText to a default value
        this.afterTextChanged(Editable.Factory.getInstance().newEditable(""));
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * Replace any text entered with a formatted currency value.
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        EditText editText = editTextWeakReference.get();
        if (editText == null) return;

        String s = editable.toString();
        Log.d("ValueTextWatcher", String.format("called with Editable: '%s'", s));

        // get rid of non digits
        String digits = s.replaceAll("[\\D]", "");

        // consider all digits entered to be the number of cents
        Long centsNum;
        try {
            if (digits.equals("")) {
                centsNum = 0L;
            } else {
                centsNum = Long.parseLong(digits);
            }
        } catch (NumberFormatException e) {
            // long too long :/
            centsNum = Long.MAX_VALUE;
        }

        // separate value into dollars and cents
        long sepDollarsNum = centsNum / 100;
        long sepCentsNum = centsNum - 100 * sepDollarsNum;

        // using Locale.CANADA here makes a lot of assumptions, however dealing with different
        // currency formats is not within the scope of this project
        Log.d("ValueEditText", String.format("Formatting values: %d, %d", sepDollarsNum, sepCentsNum));
        String formatted = String.format(Locale.CANADA, "$%d.%02d", sepDollarsNum, sepCentsNum);

        // replace contents of EditText with new formatted string
        editText.removeTextChangedListener(this);
        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }
}