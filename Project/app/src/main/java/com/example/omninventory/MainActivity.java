package com.example.omninventory;

import static android.content.ContentValues.TAG;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
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
    private String currentUser;
    public static final String EXTRA_LOGIN_USERNAME = "EXTRA_LOGIN_USERNAME";
    private ActivityResultLauncher<Intent> loginActivityResultLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // === get references to Views
        final ListView itemList = findViewById(R.id.item_list);
        final TextView titleText = findViewById(R.id.title_text);
        final ImageButton profileBtn = findViewById(R.id.profile_button);

        // === UI setup
        // set title text
        titleText.setText(getString(R.string.main_title_text));

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_main, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

        // === set up itemList
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
                if (currentUser != null) {
                    // start Profile activity
                }
                else {
                    startLoginActivity();
                }
            }
        });
    }

    private void startLoginActivity() {
        loginActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        String username = result.getData().getStringExtra(EXTRA_LOGIN_USERNAME);
                        this.currentUser = username;
                    }
                }
        );
        Intent intent = new Intent(this, LoginActivity.class);
        loginActivityResultLauncher.launch(intent);
    }
}