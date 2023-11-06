package com.example.omninventory;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

import java.lang.ref.WeakReference;
import java.util.Locale;

/**
 * With help from:
 * https://stackoverflow.com/questions/5107901/better-way-to-format-currency-input-edittext
 */
public class ValueTextWatcher implements TextWatcher {
    private final WeakReference<EditText> editTextWeakReference;

    public ValueTextWatcher(EditText editText) {
        // initialize with reference to EditText to watch
        editTextWeakReference = new WeakReference<EditText>(editText);

        // unnecessary
        // this.afterTextChanged(Editable.Factory.getInstance().newEditable(editText.getText()));
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

        // convert whatever we have to a string representation of a valid currency value
        String formatted = InventoryItemValue.makeValidString(s);

        // replace contents of EditText with new formatted string
        editText.removeTextChangedListener(this);
        editText.setText(formatted);
        editText.setSelection(formatted.length());
        editText.addTextChangedListener(this);
    }
}