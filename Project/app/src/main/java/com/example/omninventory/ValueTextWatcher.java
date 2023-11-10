package com.example.omninventory;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;

/**
 * Custom TextWatcher that reformats any input entered into an EditText intended to hold currency
 * values, resulting in text that is a valid string representation of an ItemValue (see ItemValue
 * class).
 * @author Castor
 */
public class ValueTextWatcher implements TextWatcher {
    // with help/inspiration from: https://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext
    private final WeakReference<EditText> editTextWeakReference;

    /**
     * Constructor that sets up a reference to the EditText being watched.
     * @param editText The EditText being watched.
     */
    public ValueTextWatcher(EditText editText) {
        // initialize with reference to EditText to watch
        editTextWeakReference = new WeakReference<EditText>(editText);
    }

    /**
     * Unused method, necessary to implement TextWatcher.
     * @param s
     * @param start
     * @param count
     * @param after
     */
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    /**
     * Unused method, necessary to implement TextWatcher.
     * @param s
     * @param start
     * @param before
     * @param count
     */
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    /**
     * Replace any text entered with a valid currency value.
     * @param editable
     */
    @Override
    public void afterTextChanged(Editable editable) {
        // get text from watched EditText
        EditText editText = editTextWeakReference.get();
        if (editText == null) return;

        String s = editable.toString();
        Log.d("ValueTextWatcher", String.format("called with Editable: '%s'", s));

        // convert whatever we have to a string representation of a valid currency value
        String formatted = ItemValue.makeValidString(s);

        // replace contents of EditText with new formatted string
        editText.removeTextChangedListener(this);
        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }
}