package com.example.omninventory;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Main screen of the app; holds list of inventory items and buttons
 * that take user to other screens.
 */
public class MainActivity extends AppCompatActivity {

    private ListView itemList;
    private ArrayList<InventoryItem> itemListData;
    private InventoryItemAdapter itemListAdapter;
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // === get references to Views
        final ListView itemList = findViewById(R.id.item_list);
        final TextView titleText = findViewById(R.id.title_text);

        // === UI setup
        // set title text
        titleText.setText(getString(R.string.main_title_text));

        // === this will store user's login state to keep them logged in
        sp = getSharedPreferences("login",MODE_PRIVATE);

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_main, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout, 0);

        // get taskbar buttons
        final ImageButton profileBtn = findViewById(R.id.profile_button);

        // check if user just logged in
        if (getIntent().getExtras() != null)  {
            // user just logged in
            String user = getIntent().getExtras().getString("loggedInUser");
            sp.edit().putBoolean("logged",true).apply();
            sp.edit().putString("username",user).apply();
            Log.d("login", "Logged in as: " + user);
            // todo: for testing purposes only, will remove later
            Toast.makeText(getApplicationContext(), "Logged in as , " + user, Toast.LENGTH_LONG).show();
        }

        // === set up itemList owned by logged in user
        // TODO: this is a string array for now, fix
        itemListData = new ArrayList<InventoryItem>();
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);

//        itemListData.add(new InventoryItem("Cat"));

        // === set up onClick actions
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("MainActivity", "click");

                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        profileBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // todo: this logs out the current user, for testing only, will remove this later
                sp.edit().putBoolean("logged",false).apply();
                // check if user is logged in
                if (!sp.getBoolean("logged",false)) {
                    startLoginActivity();
                }
                else {
                    // start ProfileActivity
                }
            }
        });
    }

    private void startLoginActivity() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}