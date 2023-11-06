package com.example.omninventory;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;
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
        if (s.isEmpty()) return;

        Log.d("ValueTextWatcher", s);

        editText.removeTextChangedListener(this);

        String digits = s.replaceAll("[\\D]", ""); // get rid of non digits

        // take all digits as cents
        Long centsNum;
        try {
            if (digits == "") {
                centsNum = 0L;
            } else {
                centsNum = Long.parseLong(digits);
            }
        } catch (NumberFormatException e) {
            // long too long :/
            centsNum = Long.MAX_VALUE;
        }

        // separate into dollars and cents
        Long sepDollarsNum = centsNum / 100;
        Long sepCentsNum = centsNum - 100 * sepDollarsNum;

        // replace contents with new formatted string
        String formatted = String.format("%d.%02d", sepDollarsNum, sepCentsNum);
        editText.setText(formatted);
        editText.setSelection(formatted.length());

        editText.addTextChangedListener(this);
    }
}