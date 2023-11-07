package com.example.omninventory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.firestore.ListenerRegistration;

import java.util.ArrayList;

/**
 * Main screen of the app. Holds list of inventory items and buttons
 * that take user to other screens.
 */
public class MainActivity extends AppCompatActivity {

    private InventoryRepository repo;
    private ArrayList<InventoryItem> itemListData;
    private ArrayList<InventoryItem> completeItemList;
    private InventoryItemAdapter itemListAdapter;
    private String currentUser;
    private String sortBy;
    private String filterMake;
    private ItemDate filterStartDate;
    private ItemDate filterEndDate;
    private String filterDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // === set up database
        repo = new InventoryRepository();

        // === get references to Views
        final ListView itemList = findViewById(R.id.item_list);
        final TextView titleText = findViewById(R.id.title_text);

        // === UI setup
        titleText.setText(getString(R.string.main_title_text)); // set title text

        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.taskbar_main, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.task_bar_main);
        taskbarHolder.addView(taskbarLayout);

        itemListData = new ArrayList<InventoryItem>();
        // retrieve data passed from SortFilterActivity: itemListData and sortBy

        Intent intent = getIntent();
        if (intent != null) {
            if (intent.getSerializableExtra("itemListData") != null) {
                itemListData = (ArrayList<InventoryItem>) intent.getSerializableExtra("itemListData");
                completeItemList = (ArrayList<InventoryItem>) itemListData.clone();
            }
            if (intent.getStringExtra("sortBy") != null) {
                sortBy = intent.getStringExtra("sortBy");
            }
            if (intent.getStringExtra("filterMake") != null) {
                filterMake = intent.getStringExtra("filterMake");
            }
            if (intent.getStringExtra("filterStartDate") != null) {
                filterStartDate = (ItemDate) intent.getSerializableExtra("filterStartDate");
            }
            if (intent.getStringExtra("filterEndDate") != null) {
                filterEndDate = (ItemDate) intent.getSerializableExtra("filterEndDate");
            }
            if (intent.getStringExtra("filterDescription") != null) {
                filterDescription = intent.getStringExtra("filterDescription");
            }
        }

        // connect itemList to Firestore database
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);
        ListenerRegistration registration = repo.setupInventoryItemList(itemListAdapter); // set up listener for getting Firestore data

        if (sortBy != null) {
            // should always trigger if coming from SortFilterActivity
            registration.remove();
            SortFilterActivity.applySorting(sortBy, itemListAdapter);
        }
        if (filterMake != null) {
            SortFilterActivity.applyMakeFilter(filterMake, itemListAdapter);
        }
        if (filterStartDate != null && filterEndDate != null) {
            SortFilterActivity.applyDateFilter(filterStartDate, filterEndDate, itemListAdapter);
        }
        if (filterDescription != null) {
                SortFilterActivity.applyDescriptionFilter(filterDescription, itemListAdapter);
        }

        // === set up onClick actions
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("MainActivity", "click");
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("item", itemListData.get(position));
                startActivity(intent);
            }
        });

        ImageButton sortFilterBtn = findViewById(R.id.sort_filter_button);
        sortFilterBtn.setOnClickListener((v) -> {
            Intent myIntent = new Intent(MainActivity.this, SortFilterActivity.class);
            if (completeItemList == null) {
                myIntent.putExtra("itemListData", itemListData);
            }
            else {
                myIntent.putExtra("itemListData", completeItemList);
            }
            MainActivity.this.startActivity(myIntent);
        });
    }
    
    // add user to database
    // todo: hash password
//    public void onSignUpOKPressed(User user) {
//        // Check if username already exists
//        DocumentReference userDocRef = db.collection("users").document(user.getUsername());
//        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if (task.isSuccessful()) {
//                    DocumentSnapshot document = task.getResult();
//                    if (document.exists()) {
//                        // toast displaying error message
//                        Toast.makeText(getApplicationContext(),"Username already exists", Toast.LENGTH_SHORT).show();
//                    } else {
//                        Log.d(TAG, "Username is available");
//                        HashMap<String, Object> data = new HashMap<>();
//                        data.put("Password", user.getPassword());
//                        usersRef
//                                .document(user.getUsername())
//                                .set(data)
//                                .addOnSuccessListener(new OnSuccessListener<Void>() {
//                                    @Override
//                                    public void onSuccess(Void aVoid) {
//                                        Log.d("Firestore", "DocumentSnapshot successfully written!");
//                                    }
//                                });
//                    }
//                } else {
//                    Log.d(TAG, "Failed with: ", task.getException());
//                }
//            }
//        });
//    }

    // todo: document ID is auto-generated for now, may change later
//    public void onAddItemOKPressed(InventoryItem item) {
//        HashMap<String, Object> data = new HashMap<>();
//        data.put("user", currentUser);
//        data.put("description", item.getDescription());
//        data.put("comment", item.getComment());
//        data.put("make", item.getMake());
//        data.put("model", item.getModel());
//        data.put("serialno", item.getSerialno());
//        data.put("value", item.getValue());
//        data.put("date", item.getDate());
//        // tags and images go here
//
//        db.collection("inventoryItems")
//            .add(data)
//            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                @Override
//                public void onSuccess(DocumentReference documentReference) {
//                    Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
//                }
//            })
//            .addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    Log.w(TAG, "Error adding document", e);
//                }
//            });
//    }
}