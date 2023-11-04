package com.example.omninventory;

import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.view.WindowCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import com.example.omninventory.databinding.ActivitySortFilterBinding;

public class SortFilterActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private AppBarConfiguration appBarConfiguration;
    private ActivitySortFilterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        final TextView titleText = findViewById(R.id.title_text);
        titleText.setText(getString(R.string.sort_filter_title_text));

        final Spinner sortDropdown = (Spinner) findViewById(R.id.sort_dropdown_spinner);
        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.sort_dropdown_options,
                android.R.layout.simple_spinner_item
        );
        // Specify the layout to use when the list of choices appears.
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner.
        sortDropdown.setAdapter(adapter);
        sortDropdown.setOnItemSelectedListener(this);

    }

    // called when item in dropdown selected
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        // An item is selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos).
    }

    // called when nothing in dropdown selected
    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback.
    }

}