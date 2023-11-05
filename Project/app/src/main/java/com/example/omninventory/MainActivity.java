package com.example.omninventory;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    private ArrayList<InventoryItem> itemListData;
    private InventoryItemAdapter itemListAdapter;
    private FirebaseFirestore db;
    private CollectionReference inventoryItemRef;
    private CollectionReference usersRef;
    private String currentUser;

    private ListView itemList;

    private TextView titleText;

    private ImageButton deleteItemButton;

    private ArrayList<InventoryItem> selectedItems;

    private Dialog deleteDialog;

    private void deleteDialog() {
        deleteDialog.setCancelable(false);
        deleteDialog.setContentView(R.layout.delete_dialog);

        // UI Elements of Dialog
        TextView deleteItemsText = deleteDialog.findViewById(R.id.delete_message);
        Button deleteButton = deleteDialog.findViewById(R.id.delete_dialog_button);
        Button cancelButton = deleteDialog.findViewById(R.id.cancel_dialog_button);

        // Fill out TextView with information
        String defaultText = "Are you sure you would like to delete the selected items (";
        int index = 0;
        for (InventoryItem selectedItem: selectedItems) {
            defaultText += selectedItem.getName();
            if (index == selectedItems.size()-1) {
                defaultText += ")";
            } else {
                defaultText += ", ";
            }
            index += 1;
        }
        deleteItemsText.setText(defaultText);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for (InventoryItem selectedItem: selectedItems) {
                    itemListData.remove(selectedItem);
                    itemListAdapter.notifyDataSetChanged();
                }
                deleteDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteDialog.dismiss();
            }
        });

        deleteDialog.show();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Set up database
        db = FirebaseFirestore.getInstance();
        inventoryItemRef = db.collection("inventoryItems");

        // Get references to views
        itemList = findViewById(R.id.item_list);
        titleText = findViewById(R.id.title_text);
        deleteItemButton = findViewById(R.id.deleteItemButton);

        // Setup delete items dialog
        deleteDialog = new Dialog(this);

        // UI setup
        titleText.setText(getString(R.string.main_title_text));

        // Set up itemList
        itemListData = new ArrayList<InventoryItem>();
        itemListAdapter = new InventoryItemAdapter(this, itemListData);
        itemList.setAdapter(itemListAdapter);

        itemListData.add(new InventoryItem("Cat", "beloved family pet"));
        itemListData.add(new InventoryItem("Laptop", "for developing android apps <3"));
        itemListData.add(new InventoryItem("301 Group Members", "their names are Castor, Patrick, Kevin, Aron, Rose, and Zachary. this item has a long name and description so we can see what that looks like"));

        // Set up list of selected items
        selectedItems = new ArrayList<InventoryItem>();

        // Set up onClick actions
        itemList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Log.d("MainActivity", "click");
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                intent.putExtra("item", itemListData.get(position));
                startActivity(intent);
            }
        });

        itemList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                InventoryItem item = itemListData.get(position);
                if (item.isSelected()) {
                    item.setSelected(false);
                    view.setBackgroundColor(Color.WHITE);
                    selectedItems.remove(item);
                } else {
                    item.setSelected(true);
                    view.setBackgroundColor(Color.LTGRAY);
                    selectedItems.add(item);
                }
                itemListAdapter.notifyDataSetChanged();
                return true;
            }
        });

        deleteItemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedItems.size() > 0) {
                    deleteDialog();
                }
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
        data.put("serialNo", item.getSerialNo());
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