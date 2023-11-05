package com.example.omninventory;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    private ArrayList<InventoryItem> itemListData;
    private InventoryItemAdapter itemListAdapter;
    private FirebaseFirestore db;
    private CollectionReference inventoryItemRef;
    private CollectionReference usersRef;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // === set up database
        db = FirebaseFirestore.getInstance();
        inventoryItemRef = db.collection("inventoryItems");

        // === get references to Views
        final ListView itemList = findViewById(R.id.item_list);
        final TextView titleText = findViewById(R.id.title_text);

        // === UI setup
        // set title text
        titleText.setText(getString(R.string.main_title_text));
        // add taskbar
        LayoutInflater taskbarInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View taskbarLayout = taskbarInflater.inflate(R.layout.main_taskbar, null);
        ViewGroup taskbarHolder = (ViewGroup) findViewById(R.id.taskbar_holder);
        taskbarHolder.addView(taskbarLayout);

        // === set up itemList
        itemListData = new ArrayList<InventoryItem>();
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);

        itemListData.add(new InventoryItem("Cat", "beloved family pet"));
        itemListData.add(new InventoryItem("Laptop", "for developing android apps <3"));
        itemListData.add(new InventoryItem("301 Group Members", "their names are Castor, Patrick, Kevin, Aron, Rose, and Zachary. this item has a long name and description so we can see what that looks like"));

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
    }
    // add user to database
    // todo: hash password
    public void onSignUpOKPressed(User user) {
        // Check if username already exists
        DocumentReference userDocRef = db.collection("users").document(user.getUsername());
        userDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // toast displaying error message
                        Toast.makeText(getApplicationContext(),"Username already exists", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "Username is available");
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("Password", user.getPassword());
                        usersRef
                                .document(user.getUsername())
                                .set(data)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d("Firestore", "DocumentSnapshot successfully written!");
                                    }
                                });
                    }
                } else {
                    Log.d(TAG, "Failed with: ", task.getException());
                }
            }
        });
    }

    // todo: document ID is auto-generated for now, may change later
    public void onAddItemOKPressed(InventoryItem item) {
        HashMap<String, Object> data = new HashMap<>();
        data.put("user", currentUser);
        data.put("description", item.getDescription());
        data.put("comment", item.getComment());
        data.put("make", item.getMake());
        data.put("model", item.getModel());
        data.put("serialno", item.getSerialno());
        data.put("value", item.getValue());
        data.put("date", item.getDate());
        // tags and images go here
        db.collection("inventoryItems")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }
}